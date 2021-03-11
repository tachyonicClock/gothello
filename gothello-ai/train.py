import gothello
import itertools
import random
import asyncio
import neat
import pickle
import os
import numpy as np
from concurrent.futures import ThreadPoolExecutor
from haikunator import Haikunator
from progress.bar import ShadyBar


class Contendor():
    def run_model(self, input):
        return self.net.activate(input)

    def save(self, gen, file=None):
        file="networks/" + str(gen) +"-"+ Haikunator.haikunate(0) + ".pkl"

        print("SAVING NEURAL NETWORK:", file)
        with open(file, "wb") as file: 
            pickle.dump(self.net, file)

    def __init__(self, genome, config, id=0):
        self.id = id
        self.genome = genome
        self.net = neat.nn.FeedForwardNetwork.create(genome, config)

class RandomContendor():
    class DummyGenome():
        fitness = 0
    def run_model(self, input):
        return np.random.rand(65)

    def __init__(self):
        self.genome = self.DummyGenome()

class Result():
    def __init__(self, winner, id_a, id_b):
        self.winner = winner
        self.id_a = id_a
        self.id_b = id_b

def tournament(contendors):
    sample = 200
    # threads = 4
    n = len(contendors)

    combinations = list(itertools.combinations(range(len(contendors)), 2))
    num_combs = len(combinations)

    if sample < num_combs:
        random.shuffle(combinations)

    sample = min(num_combs, sample)
    print()
    print("---- CONDUCTING TOURNAMENT ----")
    print("contendors: {}, combinations: {}, sample: {:0.2f}% {:d}".format(
        n, num_combs, sample/num_combs * 100, sample))

    bar = ShadyBar('', max=sample, suffix = '%(percent).1f%% - avg: %(avg).2fs eta: %(eta)ds')
    results = []
    for i in range(sample):
        a, b = combinations[i]

        a = contendors[a]
        b = contendors[b]
        gg = gothello.GothelloGame(a.run_model, b.run_model, a.id, b.id)

        winner = asyncio.get_event_loop().run_until_complete(gg.play_game())
        results.append(Result(winner, a.id, b.id))
        bar.next()
    bar.finish()

    scores = [0] * n
    games_played = [0] * n
    for i in range(len(results)):
        a, b = combinations[i]

        games_played[a] += 1
        games_played[b] += 1

        winner = results[i].winner
        if winner == "A":
            scores[a] += 1
        elif winner == "B":
            scores[b] += 1
        elif winner == "AB":
            scores[a] += 0.5
            scores[b] += 0.5
        else:
            raise Exception("Unexpected winner", winner)
    # Set genome fitness
    for i in range(n):
        if games_played[i] != 0:
            fitness = scores[i]/games_played[i]
        else:
            fitness = 0.0
        print("Model [{:2d}] won {:2.0f} played {:2d} scored {:1.2f}".format(contendors[i].id, scores[i], games_played[i], fitness))
        contendors[i].genome.fitness = fitness
    print()
    print()

def eval_genomes(genomes, config):
    contendors = []
    for genome_id, genome in genomes:
        contendors.append(Contendor(genome, config, genome_id))
    tournament(contendors)

CHECKPOINTS = 5

def run(config_file):
    # Load configuration.
    config = neat.Config(neat.DefaultGenome, neat.DefaultReproduction,
                         neat.DefaultSpeciesSet, neat.DefaultStagnation,
                         config_file)

    # Create the population, which is the top-level object for a NEAT run.
    p = neat.Population(config)

    # Add a stdout reporter to show progress in the terminal.
    p.add_reporter(neat.StdOutReporter(True))
    stats = neat.StatisticsReporter()
    p.add_reporter(neat.Checkpointer(CHECKPOINTS))

    while True:
        winner = p.run(eval_genomes, 5)
        Contendor(winner, p.config).save(p.generation)

def run_continue():
    p = neat.Checkpointer.restore_checkpoint('neat-checkpoint-337')

    p.add_reporter(neat.StdOutReporter(True))
    p.add_reporter(neat.Checkpointer(CHECKPOINTS))

    while True:
        winner = p.run(eval_genomes, 5)
        Contendor(winner, p.config).save(p.generation)

if __name__ == "__main__":
    run_continue()
    # local_dir = os.path.dirname(__file__)
    # config_path = os.path.join(local_dir, 'gothello.config')
    # run(config_path)
