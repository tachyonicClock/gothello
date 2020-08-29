import pickle
import neat.nn.feed_forward

class Contendor():
    
    net = None
    id = None

    def run_model(self, input):
        return self.net.activate(input)

    def save(self, file):
        print("SAVING NEURAL NETWORK")
        with open(file, "wb") as file: 
            pickle.dump(self.net, file)

    def __init__(self, net):
        self.net = net