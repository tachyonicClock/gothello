import gothello
import itertools
import random
import asyncio
import neat
import pickle
import os
import numpy as np
from concurrent.futures import ThreadPoolExecutor


class Contendor():
    def run_model(self, input):
        return self.net.activate(input)

    def save(self, file):
        with open(file, "wb") as file: 
            pickle.dump(self, file)

    def __init__(self, genome, config):
        self.genome = genome
        self.config = config
        self.net = neat.nn.FeedForwardNetwork.create(genome, config)

class RandomContendor():
    class DummyGenome():
        fitness = 0
    def run_model(self, input):
        return np.random.rand(65)

    def __init__(self):
        self.genome = self.DummyGenome()


async def gothello_tournament_main(games_list):
    result = []
    for gg in games_list:
        result.append(gg.play_game())
    await asyncio.wait(result)
    return result


def gothello_tournament_thread(games_list):
    loop = asyncio.new_event_loop()
    return loop.run_until_complete(gothello_tournament_main(games_list))


def tournament(contendors):
    sample = 200
    threads = 8
    n = len(contendors)

    combinations = list(itertools.combinations(range(len(contendors)), 2))
    num_combs = len(combinations)
    random.shuffle(combinations)

    sample = min(num_combs, sample)
    print()
    print("---- CONDUCTING TOURNAMENT ----")
    print("contendors: {}, combinations: {}, sample: {:0.2f}% {:d}".format(
        n, num_combs, sample/num_combs * 100, sample))

    print("Setting Up Threads:")
    # Initialize and run threads
    thread_future = []
    print("Running on {:d} threads with {:d} games on each".format(
        threads, sample//threads))
    with ThreadPoolExecutor(max_workers=threads) as executor:
        for thread in range(threads):

            games_list = []
            start = (sample//threads)*thread
            end = sample//threads + start

            print("Starting games {:d} to {:d}".format(start, end))
            # Add range to games_list
            for i in range(start, end):
                a, b = combinations[i]
                gg = gothello.GothelloGame(
                    contendors[a].run_model, contendors[b].run_model)
                games_list.append(gg)
                # print(gg.id)
            # print()

            # submit job to executor
            thread_future.append(executor.submit(
                gothello_tournament_thread, games_list))

    # Wait for results
    results = []
    for future in thread_future:
        results.extend(future.result())

    scores = [0] * n
    for i in range(len(results)):
        a, b = combinations[i]
        winner = results[i]
        if winner == "A":
            scores[a] += 1
        elif winner == "B":
            scores[b] += 1
        else:
            scores[a] += 0.5
            scores[b] += 0.5
    # Set genome fitness
    for i in range(n):
        contendors[i].genome.fitness = scores[i]/sample
    print()
    print()

# def eval_randoms():
#     tournament([RandomContendor()] * 10)
# eval_randoms()

def eval_genomes(genomes, config):
    contendors = []
    for genome_id, genome in genomes:
        contendors.append(Contendor(genome, config))
    tournament(contendors)


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
    p.add_reporter(stats)
    p.add_reporter(neat.Checkpointer(5))

    # Run for up to 300 generations.
    winner = p.run(eval_genomes, 50)
    Contendor(winner, config).save("genome-50")



if __name__ == "__main__":
    local_dir = os.path.dirname(__file__)
    config_path = os.path.join(local_dir, 'gothello.config')
    run(config_path)
