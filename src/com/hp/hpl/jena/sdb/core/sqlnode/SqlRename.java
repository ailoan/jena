/*
 * (c) Copyright 2007, 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.core.sqlnode;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.sparql.core.Var;

import com.hp.hpl.jena.sdb.core.*;
import com.hp.hpl.jena.sdb.core.sqlexpr.SqlColumn;
import com.hp.hpl.jena.sdb.shared.SDBInternalError;

/** SQL rename */
//Not used - may be removed
/*public*/ class SqlRename extends SqlNodeBase1
{
    private ScopeBase idScope ;
    private ScopeBase nodeScope ;
    private SqlTable vTable ;       // Our column naming space.
    private List<ColAlias> columns ;
    
    //---

    private /*public*/ static SqlNode view(String alias, SqlNode sqlNode)
    { 
        // return SqlSelectBlock.view(sqlNode) ;
        return null ;
//        SqlTable table = new SqlTable(alias) ;
//        Generator gen = Gensym.create("X") ;    // Column names.  Not global.
//        
//        SqlRename rename = new SqlRename(table, sqlNode) ;
//        rename.merge(sqlNode.getIdScope(), rename.idScope, gen) ;
//        rename.merge(sqlNode.getNodeScope(), rename.nodeScope, gen) ;
//        return rename ;
    }
    
    private SqlRename(SqlTable here, SqlNode sqlNode)
    {
        super(here.getAliasName(), sqlNode) ;
        this.vTable = here ;
        this.idScope = new ScopeBase() ;
        this.nodeScope = new ScopeBase() ;
        this.columns = new ArrayList<ColAlias>() ;
    }

    private SqlRename(SqlRename other)
    {
        super(other.vTable.getAliasName(), other.getSubNode()) ;
        this.vTable = other.vTable ;
        this.idScope = other.idScope ;
        this.nodeScope = other.nodeScope ;
        this.columns = new ArrayList<ColAlias>(other.columns) ;
    }

    // Map all vars in the scope to names in the rename.
    private void merge(Scope scope, ScopeBase newScope, Generator gen)
    {
        String x = "" ;
        String sep = "" ;

        for ( ScopeEntry e : scope.findScopes() )
        {
            SqlColumn oldCol = e.getColumn() ;
            Var v = e.getVar() ;
            String colName = gen.next() ;
            SqlColumn newCol = new SqlColumn(vTable, colName) ;
            columns.add(new ColAlias(oldCol, newCol)) ;
            newScope.setColumnForVar(v, newCol) ;
            // Annotations
            x = String.format("%s%s%s:(%s=>%s)", x, sep, v, oldCol, newCol) ;
            sep = " " ;
        }
        if ( x.length() > 0 )
            addNote(x) ;
    }

    @Override
    public Scope getIdScope()   { return idScope ; }
    @Override
    public Scope getNodeScope() { return nodeScope ; }
    
    public void visit(SqlNodeVisitor visitor)
    { throw new SDBInternalError("SqlRename.visit") ; }
    // { visitor.visit(this) ; }

    @Override
    public SqlNode apply(SqlTransform transform, SqlNode subNode)
    { throw new SDBInternalError("SqlRename.apply") ; }
    // { return transform.transform(this, subNode) ; }

    @Override
    public SqlNode copy(SqlNode subNode)
    { return new SqlRename(this) ; }
}

/*
 * (c) Copyright 2007, 2008 Hewlett-Packard Development Company, LP
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