package org.apache.solr.handler.dataimport;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;

/**
 * Created by IntelliJ IDEA.
 * User: James
 * Date: 15/08/12
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class MongoMapperTransformer extends Transformer{

    @Override
    public Object transformRow(Map<String, Object> row, Context context) {

        for (Map<String, String> map : context.getAllEntityFields()) {
            String mongoFieldName = map.get( MONGO_FIELD );
            if( mongoFieldName == null )
                continue;

            String columnFieldName = map.get( DataImporter.COLUMN );

            row.put( columnFieldName,  row.get( mongoFieldName ) );

        }


        return row;
    }

    public static final String MONGO_FIELD = "mongoField";
}
