import gothello
import itertools
import random
import asyncio
import neat
import pickle
import os
import click
from functools import partial
from contendor import Contendor
import numpy as np
from concurrent.futures import ThreadPoolExecutor
from progress.bar import ShadyBar

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

class Tournament():
    def __print_tournament_info(self, contendors, combinations, sample):
        print()
        print("---- CONDUCTING TOURNAMENT ----")
        print("contendors: {}, combinations: {}, sample: {:0.2f}% {:d}"
              .format(
                  contendors,
                  combinations,
                  sample/combinations * 100,
                  sample
              ))

    def __shuffle_sample(self):
        if self.sample_size < self.num_combs:
            random.shuffle(self.combinations)

    async def __play_games(self):
        bar = ShadyBar('', max=self.sample_size,
                       suffix='%(percent).1f%% - avg: %(avg).2fs eta: %(eta)ds')
        # Run a random sample of the tournament games
        for i in range(self.sample_size):
            a, b = self.combinations[i]
            a = self.contendors[a]
            b = self.contendors[b]

            # Reset NN between games
            a.reset()
            b.reset()

            # Play a gothello game
            gg = gothello.GothelloGame(a, b, a.id, b.id)
            await gg.play_game()
            bar.next()

        bar.finish()

    def __calculate_fitness(self):
        for i in range(self.population_size):
            a = self.contendors[i]
            if a.played != 0:
                a.genome.fitness = (a.won - a.lost - 0.5 * a.expired)/a.played
            else:
                a.genome.fitness = 0.0

    def __print_population_summary(self):
        print("--- Tournament complete ---")
        for i in range(self.population_size):
            a = self.contendors[i]
            print("Model [{:2d}] won {:2d}, lost {:2d}, played {:2d}, expired {:2d}, fitness {:1.2f}"
                  .format(a.id, a.won, a.lost, a.played, a.expired, a.genome.fitness))
        print()

    def run(self):
        self.__print_tournament_info(
            self.population_size, self.num_combs, self.sample_size)
        asyncio.get_event_loop().run_until_complete(self.__play_games())
        self.__calculate_fitness()
        self.__print_population_summary()

    def __init__(self, contendors, sample):
        self.sample_size = sample
        self.contendors = contendors
        self.population_size = len(contendors)
        self.combinations = list(
            itertools.combinations(range(len(contendors)), 2))
        self.num_combs = len(self.combinations)

        self.sample_size = min(self.num_combs, self.sample_size)
        self.__shuffle_sample()


def eval_genomes(genomes, config, sample=100):
    contendors = []
    for genome_id, genome in genomes:
        contendors.append(Contendor.new_recurrent(genome, config, genome_id))
    Tournament(contendors, sample).run()

def run(p, checkpoint_rate, max_sample):
    p.add_reporter(neat.StdOutReporter(True))
    p.add_reporter(neat.Checkpointer(checkpoint_rate))

    while True:
        # partial is used to pass arguments to callback. See curryfication
        winner = p.run(partial(eval_genomes, sample=max_sample), checkpoint_rate)
        best = Contendor.new_recurrent(winner, p.config)
        best.save_randomname(p.generation)

@click.command()
@click.option("--neat-config", "--conf",
              default='gothello.config',
              help='Neat config file')
@click.option("--checkpoint-rate", "-c",
              type=click.INT,
              default=5,
              help='How many generations between checkpoints')
@click.option("--max-sample", "-s",
              type=click.INT,
              default=200,
              help='How many games are played each generation')
def train(neat_config, checkpoint_rate, max_sample):
    """Begin training"""
    config = neat.Config(neat.DefaultGenome, neat.DefaultReproduction,
                         neat.DefaultSpeciesSet, neat.DefaultStagnation,
                         neat_config)

    p = neat.Population(config)
    run(p, checkpoint_rate, max_sample)


@click.command()
@click.option("--checkpoint-rate", "-c",
              type=click.INT,
              default=5,
              help='How many generations between checkpoints')
@click.option("--file", "-f",
                required=True,
              help='pkl file containing a checkpoint of a previous training session')
@click.option("--max-sample", "-s",
              type=click.INT,
              default=200,
              help='How many games are played each generation')
def resume(checkpoint_rate, file, max_sample):
    """Resume a previous training session using a checkpoint"""
    p = neat.Checkpointer.restore_checkpoint(file)
    run(p, checkpoint_rate, max_sample)

@click.group()
def cli():
    pass

if __name__ == "__main__":
    cli.add_command(train)
    cli.add_command(resume)
    cli()
