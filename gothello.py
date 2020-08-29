import asyncio
import websockets
import json
import numpy as np
import requests
import itertools



API_ENDPOINT = "http://localhost:8080/api/v0"
WS_ENDPOINT  = "ws://localhost:8080/api/v0"


class ShouldPass(Exception):
    pass


class GothelloPlayer:
    state = {}
    board_width = 8
    board_size = 64
    is_game_over = False
    verbose = False
    turn_number = 0

    def print_board(self, state):
        print("#", state["turnNumber"])
        for row in state["board"]:
            for cell in row:
                print(cell + " ", end="")
            print()

    def getXY(self, i: int):
        return i % self.board_width, i // self.board_width

    def output_layer_to_move(self, state, output_layer: np.ndarray):
        '''output_layer_to_move takes the most activated neuron and interprets it as a move'''
        ordered_index = output_layer.argsort()
        for i in ordered_index[::-1]:

            # The 65th neuron is reserved for passing
            if i == self.board_size:
                raise ShouldPass()

            x, y = self.getXY(i)
            if state["board"][y][x] == "L":
                return x, y
        raise ShouldPass()

    def get_input_layer(self, state) -> np.ndarray:
        n = self.board_size * 3
        offset_my_stones = 0
        offset_opp_stones = self.board_size
        offset_legal_moves = self.board_size * 2
        input_layer = np.zeros([n])
        i = 0

        if state["yourStones"] == "BLACK":
            my_stones = "B"
            opp_stones = "W"
        else:
            my_stones = "W"
            opp_stones = "B"

        # my stones
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
        await self.websocket.send(json.dumps({"messageType": "playStone", "row": int(y), "col": int(x)}))

    async def passTurn(self):
        await self.websocket.send(json.dumps({"messageType": "pass"}))

    async def run_model(self, state):
        try:
            if state["yourTurn"]:
                # await asyncio.sleep(0.1)
                # self.print_board(state)

                # RUN TRAINED MODEL AS CALLBACK
                output_layer = self.model(self.get_input_layer(state))

                x, y = self.output_layer_to_move(state, output_layer)

                # print("Making Play", x, y)
                await self.playStone(x, y)
        except ShouldPass as err:
            # print("Passed Turn")
            await self.passTurn()
        # print()

    async def handle_message(self, msg):
        message_type = msg["messageType"]
        if message_type == "state":
            self.turn_number = msg["turnNumber"]
            await self.run_model(msg)
        elif message_type == "status":
            if msg["variant"] != "INFO" and msg["variant"] != "SUCCESS":
                print("Server Status:", msg["variant"], msg["message"])
        elif message_type == "gameOver":
            # Set variable describing who won!
            self.is_winner = msg["isWinner"]
            self.is_draw   = (msg["winner"] == "DRAW")
            self.is_game_over = True
            await self.websocket.close()
        else:
            print("Unknown Message: ", msg)

    async def start(self):
        try:
            while not self.is_game_over:
                message = await self.websocket.recv()
                await self.handle_message(json.loads(message))
        except websockets.exceptions.ConnectionClosedOK as err:
            pass

    async def connect_to_ws(self):
        self.websocket = await websockets.connect(self.endpoint)

    @classmethod
    async def create(cls, endpoint, model):
        self = GothelloPlayer()
        self.model = model
        self.endpoint = endpoint
        await self.connect_to_ws()
        return self

class GothelloGame():

    def __init__(self, model_a, model_b):
        r = requests.get(API_ENDPOINT + "/game/new")
        self.id = r.json()['id']

        self.model_a = model_a
        self.model_b = model_b

    def get_endpoint(self):
        return WS_ENDPOINT + "/game/" + str(self.id) + "/socket"

    async def play_game(self):
        self.player_a = await GothelloPlayer.create(self.get_endpoint(), self.model_a)
        self.player_b = await GothelloPlayer.create(self.get_endpoint(), self.model_b)
        await asyncio.gather(self.player_a.start(), self.player_b.start())
        print("[{:10d}] Played Game, model {:2s} won, # turns {}".format(self.id, self.get_winner(), self.player_a.turn_number))
        return self.get_winner()

    def get_winner(self):
        if not self.player_a.is_game_over:
            raise Exception("Game not over")
        if self.player_a.is_draw:
            return "AB"
        if self.player_a.is_winner:
            return "A"
        if self.player_b.is_winner:
            return "B"


def random_model(input):
    return np.random.rand(65)



# async def main():
#     # print(GothelloGame().id)
#     gg = GothelloGame(random_model, random_model)
#     await gg.create_players()
#     input("Press Enter to continue...")
#     await gg.play_game()
#     print( gg.get_winner(), "WON!")

#     # gg = await GothelloPlayer.create("ws://localhost:8080/api/v0/game/2128351955/socket", random_model)
#     # gg2 = await GothelloGame.create("ws://localhost:8080/api/v0/game/870714488/socket")
#     # await gg.start()
#     # input("Press Enter to continue...")
#     # await gg.playStone(2,0)
#     # await asyncio.gather(gg2.start(), gg.start())

# asyncio.run(main())
# print(GothelloGame().id)
