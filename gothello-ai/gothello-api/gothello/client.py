import requests
import json

from .gothello_exception import GothelloException

class GothelloClient():
    def __init__(self, endpoint:str):
        """Create a new gothello http client"""
        self._endpoint = endpoint

    def game(self, id:int):
        """Returns details about a specific game"""
        msg = requests.get(
            self._endpoint + "/api/v0/game/{}".format(id)).json()
        GothelloException.check_msg_for_exception(msg, "game")
        return msg

    def game_state(self, id:int):
        """Returns the games current state"""
        msg = requests.get(
            self._endpoint + "/api/v0/game/{}/state".format(id)).json()
        GothelloException.check_msg_for_exception(msg, "state")
        return msg

    def new_game(self, game_type:str="public"):
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

