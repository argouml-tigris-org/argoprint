// $Id$
// Copyright (c) 2003-2004, Mikael Albertsson, Mattias Danielsson, Per Engström,
// Fredrik Gröndahl, Martin Gyllensten, Anna Kent, Anders Olsson,
// Mattias Sidebäck.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright
//   notice, this list of conditions and the following disclaimer in the
//   documentation and/or other materials provided with the distribution.
//
// * Neither the name of the University of Linköping nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.

package org.argoprint.engine.interpreters;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.UnsupportedCallException;
import org.argoprint.engine.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Handles the call tag.
 *
 * A call looks like this:
 * &lt;ap:call what="EXPRESSION"&gt;&lt;/ap:call&gt;
 */
public class InterpreterCall extends Interpreter {

    /**
     * Constructor for this interpreter.
     *
     * @param dataSource The ArgoPrintDataSource that this Interpreter
     * should fetch data from.
     */
    public InterpreterCall(ArgoPrintDataSource dataSource) {
	super("call", dataSource);
    }

    /**
     * Processes the call tag.
     *
     * @see Interpreter#processTag(Node, Environment)
     */
    protected void processTag(Node tagNode, Environment env)
    	throws BadTemplateException, UnsupportedCallException {

	NamedNodeMap attributes = tagNode.getAttributes();
	Object returnValue = callDataSource("what", attributes, env);
        if (returnValue == null) {
            returnValue = "null";
        }
	Document document = tagNode.getOwnerDocument();
	Node parentNode = tagNode.getParentNode();
	Node fetchedData = document.createTextNode(returnValue.toString());
	parentNode.replaceChild(fetchedData, tagNode);
    }
}

