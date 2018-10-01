
# IMPORTS

# for saving and loading variable
import pickle

# regex library
import re

# for loading json file
import json

# for generic visualization
import matplotlib.pyplot as plt

# class for all the generic methods
class Utils:


    def open_data_file(absolute_file_path, open_with_permission='r'):
        return open(absolute_file_path, open_with_permission)


    def save_variable(var, file_name):
        pickle.dump(var, open(file_name + ".pkl", "wb"))


    def load_variable(file_path):
        return pickle.load(open(file_path, "rb"))


    def parse_json(line):
        return json.loads(line)

    def replace_text(pattern_replacement_dictionary, text):
        for pattern, replacement in pattern_replacement_dictionary.items():
            text = re.sub(pattern, replacement, text)
        return text

    def count_documents(iterable, criteria):
        count = 0
        for item in iterable:
            if criteria(item):
                count += 1
        return count

    def show_length_distribution(document_list, refine=10, max_length=500):
        length_list = [len(document) for document in document_list]
        plt.hist(length_list, bins=refine, range=(0, max_length))
        plt.show()

    def plot_xy_graph(x, Y, x_label, Y_label, legend):
        plt.plot(x, Y)
        plt.xlabel(x_label)
        plt.ylabel(Y_label)
        plt.legend(legend)
        plt.show()

    def load_documents_list_from_json(json_file, document_field):

        document_list = list()
        for line in json_file:
            json = Utils.parse_json(line)
            document = json.get(document_field)
            if isinstance(document, str) and len(document) > 0:
                document_list.append(document)
        return document_list