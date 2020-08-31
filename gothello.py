import asyncio
import websockets
import json
import numpy as np
import requests
import itertools
import time

# GOTHELLO ENDPOINT
API_ENDPOINT = "http://localhost:8080/api/v0"
WS_ENDPOINT = "ws://localhost:8080/api/v0"


class ShouldPass(Exception):
    pass


class GothelloPlayer:
    '''
    GothelloPlayer represents a single AI player and acts as a wrapper around
    the websocket interface of the server

    We convert JSON into input layers and then convert the output layer into
    an instruction that is sent to the server
    server --> input layer --> MODEL --> output layer --> server
    '''

    # AI constraints
    turn_limit = 200

    # Constants
    BOARD_WIDTH = 8
    BOARD_SIZE = 64

    # Properties
    is_game_over = False
    turn_number = 0

    previous_network = []

    def print_board(self, state):
        '''output the board to terminal'''
        print("#", state["turnNumber"])
        for row in state["board"]:
            for cell in row:
                print(cell + " ", end="")
            print()

    def getXY(self, i: int) -> tuple:
        '''convert a 1D index into 2D '''
        return i % self.BOARD_WIDTH, i // self.BOARD_WIDTH

    def output_layer_to_move(self, state, output_layer: np.ndarray):
        '''
        output_layer_to_move takes the most activated neuron and interprets it 
        as a move

        65 neurons
            64 represent where the network wants to play
            1  represents wheather the network should pass
        '''

        output_layer = np.array(output_layer)
        ordered_index = output_layer.argsort()

        for i in ordered_index[::-1]:
            # The 65th neuron is reserved for passing
            if i == self.BOARD_SIZE:
                raise ShouldPass()

            x, y = self.getXY(i)
            if state["board"][y][x] == "L":
                return x, y
        # in the event nothing can be done the AI will pass
        raise ShouldPass()

    def get_input_layer(self, state) -> np.ndarray:
        '''
        Converts the board state into an input layer

        192 neurons representing state
            64 represent my stones
            64 represent the opponents stone
            64 represent legal moves
        '''

        # Initialize the input layer with n neurons (192)
        n = self.BOARD_SIZE * 3
        input_layer = np.zeros([n])

        # Offsets for each class of input neuron
        offset_my_stones = 0
        offset_opp_stones = self.BOARD_SIZE
        offset_legal_moves = self.BOARD_SIZE * 2

        if state["yourStones"] == "BLACK":
            my_stones = "B"
            opp_stones = "W"
        else:
            my_stones = "W"
            opp_stones = "B"

        i = 0
        for row in state["board"]:
            for cell in row:
                # Is placing a stone on the cell legal
                if cell == "L":
                    input_layer[i + offset_legal_moves] = 1.0

                # Is the cell filled with one of my stones
                if cell == my_stones:
                    input_layer[i + offset_my_stones] = 1.0

                # Is the cell filled with an opponent cell
                if cell == opp_stones:
                    input_layer[i + offset_opp_stones] = 1.0
                i += 1
        return input_layer

    async def playStone(self, x: int, y: int):
        '''sends message to play a stone to the server'''
        await self.websocket.send(json.dumps({"messageType": "playStone", "row": int(y), "col": int(x)}))

    async def passTurn(self):
        '''sends message to pass turn to the server'''
        await self.websocket.send(json.dumps({"messageType": "pass"}))

    async def play_turn(self, state):
        '''use the provided model to play a turn'''

        # Skip turn if we have gone over the turn limit
        self.turn_number = state["turnNumber"]
        if self.turn_number > self.turn_limit:
            await self.passTurn()
            return

        # Insure it is my turn
        if not state["yourTurn"]:
            return

        try:
            # RUN TRAINED MODEL AS CALLBACK
            output_layer = self.model(self.get_input_layer(state))

            x, y = self.output_layer_to_move(state, output_layer)
            await self.playStone(x, y)
        except ShouldPass as err:
            await self.passTurn()

    async def handle_message(self, msg):
        '''handle a websocket message by first reading in the messageType'''
        message_type = msg["messageType"]

        if message_type == "state":
            await self.play_turn(msg)

        elif message_type == "status":
            # report server error
            if msg["variant"] != "INFO" and msg["variant"] != "SUCCESS":
                print("Server Status:", msg["variant"], msg["message"])

        elif message_type == "gameOver":
            # Set variable describing who won!
            self.is_winner = msg["isWinner"]
            self.is_draw = (msg["winner"] == "DRAW")
            self.is_game_over = True
            await self.websocket.close()

        else:
            print("Unknown Message: ", msg)

    async def start(self):
        '''start playing the game'''
        while not self.is_game_over:
            message = await self.websocket.recv()
            await self.handle_message(json.loads(message))

    @classmethod
    async def create(cls, endpoint, model):
        '''create a new gothello player'''
        self = GothelloPlayer()
        self.model = model
        self.endpoint = endpoint
        self.websocket = await websockets.connect(self.endpoint)
        return self


class GothelloGame():
    '''
    GothelloGame is used to run a gothello game between two models 
    '''

    def __init__(self, model_a, model_b, id_a=0, id_b=0):
        '''
        Creates a GothelloGame object that manages a game on the server
        Requires two callback functions to be used to evaluate moves for each
        player
        '''
        r = requests.get(API_ENDPOINT + "/game/new")
        self.id = r.json()['id']

        self.model_a = model_a
        self.model_b = model_b
        self.id_a = id_a
        self.id_b = id_b

    def get_endpoint(self):
        return WS_ENDPOINT + "/game/" + str(self.id) + "/socket"

    async def play_against_human(self):
        '''Adds player_a to a game to vs a human'''
        self.player_a = await GothelloPlayer.create(self.get_endpoint(), self.model_a)
        await self.player_a.start()

    async def play_game(self):
        '''Begins a game between model_a and model_b'''

        # Connect both players to server
        self.player_a = await GothelloPlayer.create(self.get_endpoint(), self.model_a)
        self.player_b = await GothelloPlayer.create(self.get_endpoint(), self.model_b)

        await asyncio.gather(self.player_a.start(), self.player_b.start())
        return self.get_winner()

    def print_stats(self):
        print("[{:10d}] Match Complete A{:3d} vs B{:3d} , model {:2s} won, # turns {}".format(
            self.id, self.id_a, self.id_b, self.get_winner(), self.player_a.turn_number))

    def get_winner(self):
        if not self.player_a.is_game_over:
            raise Exception("Game Not Over")
        if self.player_a.is_draw:
            return "AB"
        if self.player_a.is_winner:
            return "A"
        if self.player_b.is_winner:
            return "B"
