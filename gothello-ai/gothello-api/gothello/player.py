import requests
import websockets
import json
from enum import Enum

from .stone import Stone
from .gothello_exception import GothelloException

class Player():

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


class GothelloMove():
    def __init__(self, message_type: str, contents={}):
        self.msg = json.dumps({"messageType": message_type, **contents})

    def __str__(self):
        return self.msg


class WebSocketPlayer(Player):

    _ws = None
    _move = False
    _game_id = -1

    _your_turn = False
    _your_stone = Stone.NONE
    _whos_turn = Stone.NONE

    _turn_number = 0
    _board = []

    _winner = Stone.NONE
    _black_score = 0
    _white_score = 0
    _is_game_over = False

    # Callbacks used to play the game
    _game_over_callback = False
    _opponent_turn_callback = False
    _my_turn_callback = False

    def __init__(self, endpoint: str, game_id: int):
        """Create a new gothello web-socket client and connect"""
        self._endpoint = endpoint
        self._game_id = game_id
        self._ws_endpoint = "{}/api/v0/game/{}/socket".format(
            self._endpoint, game_id)

    def _handle_state(self, msg: dict):
        self._board = msg["board"]
        self._your_turn = msg["yourTurn"]
        self._turn_number = msg["turnNumber"]
        self._your_stone = Stone[msg["yourStones"]]
        self._whos_turn = Stone[msg["turn"]]

        # print(msg)

        if self._your_turn and self._my_turn_callback:
            self._my_turn_callback()

        if not self._your_turn and self._opponent_turn_callback:
            self._opponent_turn_callback()

    def _handle_game_over(self, msg: dict):
        self._winner = Stone[msg["winner"]]
        self._black_score = msg["scores"]["blackScore"]["overall"]
        self._white_score = msg["scores"]["whiteScore"]["overall"]
        self._is_game_over = True

        if self._game_over_callback:
            self._game_over_callback()

    def _handle_status(self, msg: dict):
        if msg["variant"] != "SUCCESS":
            GothelloException.check_msg_for_exception(msg, "null")

    def _handle_msg(self, msg: dict):
        message_type = msg["messageType"]

        if message_type == "state":
            self._handle_state(msg)
        if message_type == "gameOver":
            self._handle_game_over(msg)
        if message_type == "status":
            self._handle_status(msg)

    def _index_in_range(self, x: int):
        return x >= 0 and x < 8

    def _check_coord(self, x: int, y: int):
        if not self._index_in_range(x):
            raise ValueError("Invalid x index")
        if not self._index_in_range(x):
            raise ValueError("Invalid y index")

    async def _send_move(self, move: GothelloMove):
        await self._ws.send(move.msg)

    def print_board(self):
        for row in self._board:
            for cell in row:
                print(cell + " ", end="")
            print()

    def to_string(self):
        str = ""
        for row in self._board:
            for cell in row:
                str += cell + " "
            str += "\n"
        return str

    def board(self):
        return self._board

    def my_stones(self):
        """Returns my stone colour"""
        return self._your_stone

    def game_id(self):
        return self._game_id

    def get_square(self, x: int, y: int):
        self._check_coord(x, y)
        return Stone.from_char(self._board[y][x])

    def is_legal(self, x: int, y: int):
        self._check_coord(x, y)
        square = Stone.from_char(self._board[y][x])
        return square == Stone.LEGAL

    def get_turn(self):
        return self._whos_turn

    def get_turn_number(self):
        return self._turn_number

    def get_winner(self):
        return self._winner

    def score(self, stone: Stone):
        if not isinstance(stone, Stone):
            raise TypeError("Stone should be of type Stone")

        if stone == Stone.BLACK:
            stone = "B"
        elif stone == Stone.WHITE:
            stone = "W"
        else:
            raise TypeError("Stone should be either BLACK or WHITE")

        return sum(x.count(stone) for x in self._board)

    def pass_turn(self):
        return GothelloMove("pass")

    def resign(self):
        return GothelloMove("resign")

    def play_stone(self, x: int, y: int):
        """Play a stone in the players colour at x, y"""
        self._check_coord(x, y)
        return GothelloMove("playStone", {"row": y, "col": x})

    async def listen(self):
        """Listen for messages until the game is over"""
        while not self._is_game_over:

            if self._move:
                await self._move
                self._move = False

            msg = json.loads(await self._ws.recv())
            self._handle_msg(msg)

    def game_over(self, callback: callable):
        """Register callback on game over"""
        def callback_wrapper():
            callback(self)
        self._game_over_callback = callback_wrapper
        return callback

    def my_turn(self, callback: callable):
        """Register callback on my turn"""
        def callback_wrapper():
            move = callback(self)
            if not isinstance(move, GothelloMove):
                raise RuntimeError("my_turn callback should return a move")

            self._move = self._send_move(move)

        self._my_turn_callback = callback_wrapper
        return callback

    def opponent_turn(self, callback: callable):
        """Register callback called on the opponents turn"""
        def callback_wrapper():
            callback(self)
        self._opponent_turn_callback = callback_wrapper
        return callback

    async def connect(self):
        """Open a web-socket connection"""
        self._ws = await websockets.connect(self._ws_endpoint)
        return self

    async def close(self):
        await self._ws.close()
