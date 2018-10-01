# IMPORTS

# to utilize data frames
import pandas as pd

# importing the famous gensim library
import gensim

# evaluating topic modelling alg
from gensim.models.coherencemodel import CoherenceModel

# visualizing topic modeling
import pyLDAvis
import pyLDAvis.gensim
from TopicModelling.visualization.malletLdaVisPreparer import MalletLdaVisPreparer

# hide deprecation warnings
import warnings
warnings.filterwarnings("ignore",category=DeprecationWarning)

# import topic modelling constants
from  TopicModelling.constants.topicModellingConstants import TopicModellingConstants

# for measuring code execution time
from tqdm import tqdm_notebook
from tqdm import tqdm


# setting a wider pandas data frame column
pd.set_option('display.max_colwidth',500)




class TopicModeller:


    def build_lda_mallet_with_coherence( data_bundle, start_topic=2, limit_topic=3, step=1,iterations="500", state_file_dir="/tmp/"):
        coherence_values = []
        model_list = []
        corpus = data_bundle['bag_of_words']
        dictionary = data_bundle['dictionary']
        texts = data_bundle['bigram']
        for num_topics in range(start_topic, limit_topic, step):
            prefix = state_file_dir + "pre_" + str(num_topics) + "_"
            model = gensim.models.wrappers.LdaMallet(TopicModellingConstants.MALLET_PATH, corpus=corpus, num_topics=num_topics, id2word=dictionary, iterations=iterations, prefix=prefix )
            model_list.append(model)
            coherencemodel = CoherenceModel(model=model, texts=texts, dictionary=dictionary, coherence='c_v')
            coherence_values.append(coherencemodel.get_coherence())
        return model_list, coherence_values



    def get_topic_keywords_list(ldamodel):
        topic_keywords = []
        for num in range(0,ldamodel.num_topics):
            wp = ldamodel.show_topic(num)
            topic_keywords.append( ", ".join([word for word, prop in wp]))
        return topic_keywords



    def format_topics_sentences(ldamodel, data_bundle, notebook_support=False):

        # determine notebook support
        timer = None
        if notebook_support == True:
            timer = tqdm_notebook
        else:
            timer = tqdm

        # Init output
        sent_topics_df = pd.DataFrame()
        
        corpus = data_bundle['bag_of_words']
        texts = data_bundle['raw']
        topic_keywords_list = TopicModeller.get_topic_keywords_list(ldamodel)

        # Get main topic in each document
        for i, row in enumerate(timer(ldamodel[corpus], total=len(ldamodel[corpus]))):
            row = sorted(row, key=lambda x: (x[1]), reverse=True)
            # Get the Dominant topic, Perc Contribution and Keywords for each document
            topic_num, prop_topic = row[0]
            # j == 0:  => dominant topic
            sent_topics_df = sent_topics_df.append(pd.Series([int(topic_num), round(prop_topic,4), topic_keywords_list[topic_num]]), ignore_index=True)

        sent_topics_df.columns = ['Dominant_Topic', 'Perc_Contribution', 'Topic_Keywords']

        # Add original text to the end of the output
        contents = pd.Series(texts)
        sent_topics_df = pd.concat([sent_topics_df, contents], axis=1)

        # Format
        df_dominant_topic = sent_topics_df.reset_index()
        df_dominant_topic.columns = ['Document_No', 'Dominant_Topic', 'Topic_Perc_Contrib', 'Keywords', 'Text']

        return(df_dominant_topic)




    def display_topic_stats(df_topic_sents_keywords):

        # Group top 5 sentences under each topic
        sent_topics_sorteddf_mallet = pd.DataFrame()

        sent_topics_outdf_grpd = df_topic_sents_keywords.groupby('Dominant_Topic')

        for i, grp in sent_topics_outdf_grpd:
            sent_topics_sorteddf_mallet = pd.concat([sent_topics_sorteddf_mallet, grp.sort_values(['Topic_Perc_Contrib'], ascending=[0]).head(1)], axis=0)

        # Reset Index
        sent_topics_sorteddf_mallet.reset_index(drop=True, inplace=True)

        # remove text aggregation
        sent_topics_sorteddf_mallet = sent_topics_sorteddf_mallet.iloc[:,:-1]
        
        # remove doc no aggregation
        sent_topics_sorteddf_mallet = sent_topics_sorteddf_mallet.iloc[:,1:]

        # Number of Documents for Each Topic
        topic_counts = df_topic_sents_keywords['Dominant_Topic'].value_counts()

        # Percentage of Documents for Each Topic
        topic_contribution = round(topic_counts/topic_counts.sum()*100, 4)

        # Concatenate Column wise
        sent_topics_sorteddf_mallet = pd.concat([sent_topics_sorteddf_mallet, topic_counts, topic_contribution], axis=1)

        # Format
        sent_topics_sorteddf_mallet.columns = ['Topic_Num', "Topic_Perc_Contrib", "Keywords", 'Num_Documents', 'Perc_Documents']

        return sent_topics_sorteddf_mallet


    def display_visualization(lda_mallet):
        pyLDAvisData = MalletLdaVisPreparer.preare_lda_mallet_pyldavis(lda_mallet)
        return pyLDAvis.display( pyLDAvis.prepare(**pyLDAvisData))


    def display_visualization_from_state_file(file_path):
        pyLDAvisData = MalletLdaVisPreparer.prepare_lda_mallet_pyldavis_with_state_file(file_path)
        return pyLDAvis.display(pyLDAvis.prepare(**pyLDAvisData))


    def load_dictionary(path):
        return gensim.corpora.Dictionary.load(path)

    def load_lda_model(path):
        return gensim.models.wrappers.LdaMallet.load(path)

