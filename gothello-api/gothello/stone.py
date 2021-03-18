from enum import Enum

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