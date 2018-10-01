# IMPORTS

import gzip
import pandas as pd
import sklearn.preprocessing


class MalletLdaVisPreparer:

    def extract_params(statefile):
        """Extract the alpha and beta values from the statefile.

        Args:
        statefile (str): Path to statefile produced by MALLET.
        Returns:
        tuple: alpha (list), beta
        """
        with gzip.open(statefile, 'r') as state:
            params = [x.decode('utf8').strip() for x in state.readlines()[1:3]]
            return (list(params[0].split(":")[1].split(" ")), float(params[1].split(":")[1]))



    def state_to_df(statefile):
        """Transform state file into pandas dataframe.
        The MALLET statefile is tab-separated, and the first two rows contain the alpha and beta hypterparamters.

        Args:
        statefile (str): Path to statefile produced by MALLET.
        Returns:
        datframe: topic assignment for each token in each document of the model
        """
        return pd.read_csv(statefile,
                       compression='gzip',
                       sep=' ',
                       skiprows=[1,2]
                       )



    def pivot_and_smooth(df, smooth_value, rows_variable, cols_variable, values_variable):
        """
        Turns the pandas dataframe into a data matrix.
        Args:
        df (dataframe): aggregated dataframe
        smooth_value (float): value to add to the matrix to account for the priors
        rows_variable (str): name of dataframe column to use as the rows in the matrix
        cols_variable (str): name of dataframe column to use as the columns in the matrix
        values_variable(str): name of the dataframe column to use as the values in the matrix
        Returns:
        dataframe: pandas matrix that has been normalized on the rows.
        """
        matrix = df.pivot(index=rows_variable, columns=cols_variable, values=values_variable).fillna(value=0)
        matrix = matrix.values + smooth_value

        normed = sklearn.preprocessing.normalize(matrix, norm='l1', axis=1)

        return pd.DataFrame(normed)



    def construct_mallet_state_file_path(ldamallet):
        return ldamallet + "state.mallet.gz"



    def preare_lda_mallet_pyldavis(ldamallet):
        return MalletLdaVisPreparer.prepare_lda_mallet_pyldavis_with_state_file(MalletLdaVisPreparer.construct_mallet_state_file_path(ldamallet.prefix))


    def prepare_lda_mallet_pyldavis_with_state_file(path_to_mallet_gzip):

        params = MalletLdaVisPreparer.extract_params(path_to_mallet_gzip)

        alpha = [float(x) for x in params[0][1:]]
        beta = params[1]

        df = MalletLdaVisPreparer.state_to_df(path_to_mallet_gzip)
        df['type'] = df.type.astype(str)


        # Get document lengths from statefile
        docs = df.groupby('#doc')['type'].count().reset_index(name ='doc_length')


        # Get vocab and term frequencies from statefile
        vocab = df['type'].value_counts().reset_index()
        vocab.columns = ['type', 'term_freq']
        vocab = vocab.sort_values(by='type', ascending=True)

        phi_df = df.groupby(['topic', 'type'])['type'].count().reset_index(name ='token_count')
        phi_df = phi_df.sort_values(by='type', ascending=True)
        phi = MalletLdaVisPreparer.pivot_and_smooth(phi_df, beta, 'topic', 'type', 'token_count')

        theta_df = df.groupby(['#doc', 'topic'])['topic'].count().reset_index(name ='topic_count')
        theta = MalletLdaVisPreparer.pivot_and_smooth(theta_df, alpha , '#doc', 'topic', 'topic_count')

        data = {}

        data['topic_term_dists'] = phi
        data['doc_topic_dists'] = theta
        data['doc_lengths'] = list(docs['doc_length'])
        data['vocab'] = list(vocab['type'])
        data['term_frequency'] = list(vocab['term_freq'])

        return data

