/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.query.text;

import org.apache.jena.query.text.assembler.TextVocab ;
import org.apache.lucene.store.Directory ;
import org.apache.solr.client.solrj.SolrServer ;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.DatasetFactory ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils ;
import com.hp.hpl.jena.sparql.util.Context ;

public class TextDatasetFactory
{
    static { TextQuery.init(); }
    
    /** Use an assembler file to build a dataset with text search capabilities */ 
    public static Dataset create(String assemblerFile)
    {
        return (Dataset)AssemblerUtils.build(assemblerFile, TextVocab.textDataset) ;
    }

    /** Create a text-indexed dataset */ 
    public static Dataset create(Dataset base, TextIndex textIndex)
    {
        return create(base, textIndex, false);
    }
    
    /** Create a text-indexed dataset, optionally allowing the text index to be closed if the Dataset is */
    public static Dataset create(Dataset base, TextIndex textIndex, boolean closeIndexOnDSGClose)
    {
        DatasetGraph dsg = base.asDatasetGraph() ;
        dsg = create(dsg, textIndex, closeIndexOnDSGClose) ;
        return DatasetFactory.create(dsg) ;
    }


    /** Create a text-indexed DatasetGraph */ 
    public static DatasetGraph create(DatasetGraph dsg, TextIndex textIndex)
    {
        return create(dsg, textIndex, false);
    }
    
    /** Create a text-indexed DatasetGraph, optionally allowing the text index to be closed if the DatasetGraph is */
    public static DatasetGraph create(DatasetGraph dsg, TextIndex textIndex, boolean closeIndexOnDSGClose)
    {
        TextDocProducer producer = new TextDocProducerTriples(textIndex.getDocDef(), textIndex) ;
        DatasetGraph dsgt = new DatasetGraphText(dsg, textIndex, producer, closeIndexOnDSGClose) ;
        // Also set on dsg
        Context c = dsgt.getContext() ;
        c.set(TextQuery.textIndex, textIndex) ;
        
        return dsgt ;
    }
    
    /** Create a Lucene TextIndex */ 
    public static TextIndex createLuceneIndex(Directory directory, EntityDefinition entMap)
    {
        TextIndex index = new TextIndexLucene(directory, entMap) ;
        return index ; 
    }

    /** Create a text-indexed dataset, using Lucene */ 
    public static Dataset createLucene(Dataset base, Directory directory, EntityDefinition entMap)
    {
        TextIndex index = createLuceneIndex(directory, entMap) ;
        return create(base, index, true) ; 
    }

    /** Create a text-indexed dataset, using Lucene */ 
    public static DatasetGraph createLucene(DatasetGraph base, Directory directory, EntityDefinition entMap)
    {
        TextIndex index = createLuceneIndex(directory, entMap) ;
        return create(base, index, true) ; 
    }

    /** Create a Solr TextIndex */ 
    public static TextIndex createSolrIndex(SolrServer server, EntityDefinition entMap)
    {
        TextIndex index = new TextIndexSolr(server, entMap) ;
        return index ; 
    }

    /** Create a text-indexed dataset, using Solr */ 
    public static Dataset createSolrIndex(Dataset base, SolrServer server, EntityDefinition entMap)
    {
        TextIndex index = createSolrIndex(server, entMap) ;
        return create(base, index, true) ; 
    }

    /** Create a text-indexed dataset, using Solr */ 
    public static DatasetGraph createSolrIndex(DatasetGraph base, SolrServer server, EntityDefinition entMap)
    {
        TextIndex index = createSolrIndex(server, entMap) ;
        return create(base, index, true) ; 
    }
}

