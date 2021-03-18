import pickle
from haikunator import Haikunator
import neat.nn.feed_forward

class Contendor():
    """Contendor is an object containing a neural network"""
    net = None
    id = None

    def run_model(self, input):
        """Accepts the input layer and returns the output layer of the neural network"""
        return self.net.activate(input)

    def save(self, file):
        print("SAVING NEURAL NETWORK")
        with open(file, "wb") as file: 
            pickle.dump(self.net, file)

    def save_randomname(self, gen):
        network_type = "unknown"
        if isinstance(self.net, neat.nn.recurrent.RecurrentNetwork):
            network_type = "rec"
        if isinstance(self.net, neat.nn.FeedForwardNetwork):
            network_type = "ffw"

        file="networks/" + str(gen) +"-" + network_type + "-" + Haikunator.haikunate(0) + ".pkl"
        self.save(file)

    def __init__(self, id, genome, config, net):
        self.id = id
        self.genome = genome
        self.config = config
        self.net = net

        self.played  = 0
        self.won     = 0
        self.lost    = 0
        self.drew    = 0
        self.expired = 0

    def won_game(self):
        self.played += 1
        self.won    += 1

    def lost_game(self):
        self.played += 1
        self.lost   += 1

    def drew_game(self):
        self.played += 1
        self.drew   += 1

    def game_expired(self):
        self.played  += 1
        self.expired += 1

    def reset(self):
        if isinstance(self.net, neat.nn.recurrent.RecurrentNetwork):
            self.net.reset()

    @classmethod
    def from_net(cls, net):
        return cls(0, None, None, net)

    @classmethod
    def new_FFN(cls, genome, config, id=0):
        """Create feed forward network from genome"""
        return cls(id, genome, config, neat.nn.FeedForwardNetwork.create(genome, config))

    @classmethod
    def new_recurrent(cls, genome, config, id=0):
        """Create feed forward network from genome"""
        return cls(id, genome, config, neat.nn.RecurrentNetwork.create(genome, config))
