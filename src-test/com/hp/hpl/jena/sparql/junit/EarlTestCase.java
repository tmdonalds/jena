/*
 * (c) Copyright 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sparql.junit;

import junit.framework.AssertionFailedError ;
import junit.framework.TestCase ;

import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.query.QueryFactory ;
import com.hp.hpl.jena.query.Syntax ;
import com.hp.hpl.jena.sparql.ARQException ;
import com.hp.hpl.jena.update.UpdateFactory ;
import com.hp.hpl.jena.update.UpdateRequest ;


public abstract class EarlTestCase extends TestCase
{
    protected EarlReport report = null ;
    protected String testURI = null ;
    private boolean resultRecorded = false ;
    
    protected EarlTestCase(String name, String testURI, EarlReport earl)
    { 
        super(name) ;
        this.report = earl ;
        this.testURI = testURI ;
    }
    
    public void setEARL(EarlReport earl)
    {
        this.report = earl ;
    }
    
    protected Query queryFromString(String qStr)
    {
        Query query = QueryFactory.create(qStr) ;
        return query ;
    }

    protected Query queryFromTestItem(TestItem testItem)
    {
        if ( testItem.getQueryFile() == null )
        {
            fail("Query test file is null") ;
            return null ;
        }
        
        Query query = QueryFactory.read(testItem.getQueryFile(), null, testItem.getFileSyntax()) ;
        return query ;
    }

    protected UpdateRequest updateFromString(String str)
    {
        return UpdateFactory.create(str) ;
    }

    protected UpdateRequest updateFromTestItem(TestItem testItem)
    {
        if ( testItem.getQueryFile() == null )
        {
            fail("Query test file is null") ;
            return null ;
        }

        UpdateRequest request = UpdateFactory.read(testItem.getQueryFile(), Syntax.syntaxSPARQL_11) ;
        return request ;
    }

    @Override
    final protected void runTest() throws Throwable
    { 
        try {
            runTestForReal() ;
            if ( ! resultRecorded )
                success() ;
        } catch (AssertionFailedError ex)
        { 
            if ( ! resultRecorded )
                failure() ;
            throw ex ;
        }
    }
    
    protected abstract void runTestForReal() throws Throwable ;

    protected void success()
    {
        note() ;
        if ( report == null ) return ;
        report.success(testURI) ;
    }

    protected void failure()
    {
        note() ;
        if ( report == null ) return ;
        report.failure(testURI) ;
    }

    protected void notApplicable()
    {
        note() ;
        if ( report == null ) return ;
        report.notApplicable(testURI) ;
    }
    
    protected void notTested()
    {
        resultRecorded = true ;
        if ( report == null ) return ;
        report.notTested(testURI) ;
    }
    
    private void note()
    {
        if ( resultRecorded )
            throw new ARQException("Duplictaed test results: "+getName()) ;
        resultRecorded = true ;
    }

}

/*
 * (c) Copyright 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */