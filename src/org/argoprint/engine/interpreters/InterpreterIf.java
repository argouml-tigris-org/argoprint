//$Id$
//Copyright (c) 2003, Mikael Albertsson, Mattias Danielsson, Per Engstr�m, 
//Fredrik Gr�ndahl, Martin Gyllensten, Anna Kent, Anders Olsson, 
//Mattias Sideb�ck.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without 
//modification, are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//  this list of conditions and the following disclaimer.
// 
//* Redistributions in binary form must reproduce the above copyright 
//  notice, this list of conditions and the following disclaimer in the 
//  documentation and/or other materials provided with the distribution.
//
//* Neither the name of the University of Link�ping nor the names of its 
//  contributors may be used to endorse or promote products derived from 
//  this software without specific prior written permission. 
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.

package org.argoprint.engine.interpreters;

import java.util.Vector;

import org.argoprint.ArgoPrintDataSource;
import org.argoprint.engine.Environment;
import org.w3c.dom.*;

public class InterpreterIf extends Interpreter {

	public InterpreterIf(ArgoPrintDataSource dataSource) {
		super("if", dataSource);
	}

	/**
	 * Processes the if tag.
	 * 
	 * @param tagNode
	 * @param env
	 */
	protected void processTag(Node tagNode, Environment env) throws Exception {
		// TODO: divide into some private methods

		// Evaluate condition
		NamedNodeMap attributes = tagNode.getAttributes();
		Object returnValue = callDataSource("cond", attributes, env);
		if (!(returnValue instanceof Boolean))
			throw new BadTemplateException("The condition did not evaluate to a Boolean.");
		
		NodeList ifChildren = tagNode.getChildNodes();
		
		// Find then and else nodes
		Node thenNode = null;
		Node elseNode = null;
		for (int i = 0; i < ifChildren.getLength(); i++) {
			if (isNodeNamed(ifChildren.item(i), PREFIX, "then"))
				thenNode = ifChildren.item(i);
			else if (isNodeNamed(ifChildren.item(i), PREFIX, "else"))
				elseNode = ifChildren.item(i);
		}
						
		// Decide what part of the sub tree (if any) shall be used
		NodeList resultChildren;
		if (((Boolean)returnValue).booleanValue()) {
			if (thenNode == null)
				resultChildren = ifChildren;
			else
				resultChildren = thenNode.getChildNodes();
		}
		else {
			if (elseNode == null)
				resultChildren = null;
			else
				resultChildren = elseNode.getChildNodes();
		}
		
		// Attach the result to parent and remove if tag
		Node ifParent = tagNode.getParentNode();
		if (!(resultChildren == null || resultChildren.getLength() == 0)) { 
			Node resultParent = resultChildren.item(0).getParentNode();
			Vector resultVector = getVector(resultChildren);
			for (int i = 0; i < resultVector.size(); i++) {
				Node child = (Node)resultVector.get(i);
				resultParent.removeChild(child);
				ifParent.insertBefore(child, tagNode);
			}
			recurse(resultVector, env);
		}
		ifParent.removeChild(tagNode);
	}
	

}
	
