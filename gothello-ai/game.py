from enum import Enum
class Game():

    def __init__(self):
        super().__init__()

    class Outcome():
        AGENT_A_WON = 1
        AGENT_B_WON = 2
        DRAW = 3
        TIMEOUT = 4

    def play_game(self, agent_a, agent_b):
        pass 