
# IMPORTS

# for test processing and cleaning text
import nltk
import spacy.lang.en

# topic modelling library
import gensim

# for measuring code execution time
from tqdm import tqdm_notebook
from tqdm import tqdm

# import topic modelling constants
from  TopicModelling.constants.topicModellingConstants import TopicModellingConstants

# utils
from  TopicModelling.utils.utils import Utils



#initialize elements
parser = spacy.lang.en.English()
STOP_WORDS_SET = set(nltk.corpus.stopwords.words())

class DataPreprocessor:

    def root_word(token):
        root = token.lemma_
        if root is None:
            return token
        else:
            return root

    def single_chr_emoji_eq(chr):
        eq = TopicModellingConstants.EMOJI_MAP.get(chr, None)
        if eq is None:
            return chr
        else:
            return eq

    def replace_single_emojis(text):
        return "".join([DataPreprocessor.single_chr_emoji_eq(chr) for chr in text])

    def clean_document(text):
        text = text.lower()
        text = Utils.replace_text(TopicModellingConstants.SHORT_HAND_DICTIONARY, text)
        # text = DataPreprocessor.replace_text(TopicModellingConstants.emojiMap, text )
        text = DataPreprocessor.replace_single_emojis(text)
        return text.strip()

    def clean_documents(raw_document_list, notebook_support=False):

        # determine notebook support
        timer = None
        if notebook_support == True:
            timer = tqdm_notebook
        else:
            timer = tqdm

        print("Cleaning raw documents ....")
        clean_document_list = []
        for document in timer(raw_document_list, total=len(raw_document_list)):
            cleaned_document = DataPreprocessor.clean_document(document)
            if len(cleaned_document) > TopicModellingConstants.CLEANED_DOCUMENT_THRESHOLD:
                clean_document_list.append(cleaned_document)
        print("Cleaning Done")
        return clean_document_list

    def tokenize_document(document):
        tokenized_document = []
        for token in parser(document):
            token_root = DataPreprocessor.root_word(token).lower().strip()
            if token_root not in STOP_WORDS_SET and len(token_root) > 2:
                tokenized_document.append(token_root)
        return tokenized_document

    def tokenize_documents(document_list,notebook_support=False):

        # determine notebook support
        timer = None
        if notebook_support == True:
            timer = tqdm_notebook
        else:
            timer = tqdm

        tokenized_documents = []
        for document in timer(document_list, total=len(document_list)):
            tokenized_documents.append(DataPreprocessor.tokenize_document(document))
        return tokenized_documents


    def print_data_tranformation_snapshot(data, index):
        print(data['cleaned'][index])
        print(data['tokens'][index])
        print(data['bigram'][index])
        print(data['bag_of_words'][index])

    def prepare_topic_modelling_data_bundle(raw_document_list, clean_document_list, clean_document_token_list,
                                            bigram_threshold=5, bigram_pressure=30):
        '''
        Steps to prepare bundle
        1) create bigrams from unigram document list
        2) create a document dictionary
        3) create a bag of words model
        '''
        # creating a bigram model
        bigram_model = gensim.models.phrases.Phraser(
            gensim.models.Phrases(clean_document_token_list, min_count=bigram_threshold, threshold=bigram_pressure))
        bigram = [bigram_model[feed] for feed in clean_document_token_list]

        dictionary = gensim.corpora.Dictionary(bigram)

        # corpus of list of bags of words
        bag_of_words = [dictionary.doc2bow(tokens) for tokens in bigram]

        # bind all the data together
        data = {}
        data['raw'] = raw_document_list
        data['cleaned'] = clean_document_list
        data['tokens'] = clean_document_token_list
        data['bigram'] = bigram
        data['dictionary'] = dictionary
        data['bag_of_words'] = bag_of_words
        return data

