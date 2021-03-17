import requests
import websockets
import json
from enum import Enum


class GothelloException(Exception):
    def __init__(self, msg):
        if msg["messageType"] != "status":
            super().__init__("Unexpected message type")
            return

        err = "{} {}".format(msg["variant"], msg["message"])
        super().__init__(err)

    @classmethod
    def check_msg_for_exception(cls, msg, expected_type):
        if msg["messageType"] != expected_type:
            raise GothelloException(msg)


class Stone(Enum):
    NONE = 0
    BLACK = 1
    WHITE = 2
    DRAW = 3
    SPECTATOR = 4
    LEGAL = 5
    ILLEGAL = 6

    @classmethod
    def from_char(cls, char):
        if char == "I":
            return Stone.ILLEGAL
        if char == "L":
            return Stone.LEGAL
        if char == "B":
            return Stone.BLACK
        if char == "W":
            return Stone.WHITE


class GothelloClient():
    def __init__(self, endpoint):
        """Create a new gothello http client"""
        self._endpoint = endpoint

    def game(self, id):
        """Returns details about a specific game"""
        msg = requests.get(
            self._endpoint + "/api/v0/game/{}".format(id)).json()
        GothelloException.check_msg_for_exception(msg, "game")
        return msg

    def game_state(self, id):
        """Returns the games current state"""
        msg = requests.get(
            self._endpoint + "/api/v0/game/{}/state".format(id)).json()
        GothelloException.check_msg_for_exception(msg, "state")
        return msg

    def new_game(self, game_type="public"):
        """
        Create a new game
        Parameters:
            game_type (string): public, private, single-player
        """
        msg = requests.get(self._endpoint + "/api/v0/game/new",
                           params={"type": game_type}).json()
        GothelloException.check_msg_for_exception(msg, "game")
        return msg

    def open_game(self):
        """Find an open game"""
        msg = requests.get(self._endpoint + "/api/v0/game/join").json()
        GothelloException.check_msg_for_exception(msg, "game")
        return msg


class GothelloPlayer():

    def board(self):
        pass

    def get_square(self, x, y):
        pass

    def is_legal(self, x, y):
        pass

    def get_turn(self):
        pass

    def get_turn_number(self):
        pass

    def get_winner(self):
        pass

    def is_game_over(self):
        pass

    def score(self, stone):
        pass

    def pass_turn(self):
        pass

    def resign(self):
        pass

    def play_stone(self, x, y):
        pass

    async def wait_for_my_turn(self):
        pass


class GothelloWSPlayer(GothelloPlayer):

    _ws = None

    _your_turn = False
    _your_stone = Stone.NONE
    _whos_turn = Stone.NONE

    _turn_number = 0
    _board = []

    _winner = Stone.NONE
    _black_score = 0
    _white_score = 0
    _is_game_over = False

    def __init__(self, endpoint, game_id):
        """Create a new gothello web-socket client and connect"""
        self._endpoint = endpoint
        self._ws_endpoint = "{}/api/v0/game/{}/socket".format(
            self._endpoint, game_id)

    def _handle_state(self, msg):
        self._board = msg["board"]
        self._your_turn = msg["yourTurn"]
        self._turn_number = msg["turnNumber"]
        self._your_stone = Stone[msg["yourStones"]]
        self._whos_turn = Stone[msg["turn"]]

    def _handle_game_over(self, msg):
        self._winner = msg["winner"]
        self._black_score = msg["scores"]["black"]
        self._white_score = msg["scores"]["white"]
        self._is_game_over = True

    def _handle_status(self, msg):
        if msg["variant"] != "SUCCESS":
            GothelloException.check_msg_for_exception(msg, "null")

    def _handle_msg(self, msg):
        message_type = msg["messageType"]

        if message_type == "state":
            self._handle_state(msg)
        if message_type == "gameOver":
            self._handle_game_over(msg)
        if message_type == "status":
            self._handle_status(msg)

    def _index_in_range(self, x):
        if not isinstance(x, int):
            return False
        return x >= 0 and x < 8

    def _check_coord(self, x, y):
        if not self._index_in_range(x):
            raise ValueError("Invalid x index")
        if not self._index_in_range(x):
            raise ValueError("Invalid y index")

    async def _send_message(self, message_type, contents={}):
        msg = json.dumps({"messageType": message_type, **contents})
        await self._ws.send(msg)

    async def _wait_for_new_state(self):
        while self._your_turn and not self._is_game_over:
            msg = json.loads(await self._ws.recv())
            self._handle_msg(msg)

    def print_board(self):
        for row in self._board:
            for cell in row:
                print(cell + " ", end="")
            print()

    def board(self):
        return self._board

    def my_stones(self):
        """Returns my stone colour"""
        return self._your_stone

    def get_square(self, x, y):
        self._check_coord(x, y)
        return Stone.from_char(self._board[y][x])

    def is_legal(self, x, y):
        self._check_coord(x, y)
        square = Stone.from_char(self._board[y][x])
        return square == Stone.LEGAL

    def get_turn(self):
        return self._whos_turn

    def get_turn_number(self):
        return self._turn_number

    def get_winner(self):
        return Stone[self._winner]

    def is_game_over(self):
        return self._is_game_over

    def score(self, stone):
        if not isinstance(stone, Stone):
            raise TypeError("Stone should be of type Stone")

        if stone == Stone.BLACK:
            stone = "B"
        elif stone == Stone.WHITE:
            stone = "W"
        else:
            raise TypeError("Stone should be either BLACK or WHITE")

        return sum(x.count(stone) for x in self._board)

    async def pass_turn(self):
        await self._send_message("pass")
        await self._wait_for_new_state()

    async def resign(self):
        await self._send_message("resign")
        await self._wait_for_new_state()

    async def play_stone(self, x, y):
        """Play a stone in the players colour at x, y"""
        self._check_coord(x, y)
        await self._send_message("playStone", {"row": y, "col": x})
        await self._wait_for_new_state()

    async def wait_for_my_turn(self):
        """Wait for it to be the players turn again"""

        while not self._your_turn and not self._is_game_over:
            msg = json.loads(await self._ws.recv())
            self._handle_msg(msg)

    async def connect(self):
        """Open a web-socket connection"""
        self._ws = await websockets.connect(self._ws_endpoint)
        return self

    async def close(self):
        await self._ws.close()
