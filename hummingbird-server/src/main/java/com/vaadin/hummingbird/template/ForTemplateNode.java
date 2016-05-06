/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.hummingbird.template;

import com.vaadin.hummingbird.StateNode;
import com.vaadin.hummingbird.dom.Element;
import com.vaadin.hummingbird.nodefeature.ModelList;
import com.vaadin.hummingbird.nodefeature.ModelMap;

import elemental.json.JsonObject;

/**
 * A template AST node representing the "*ngFor" looping part of an element.
 * <p>
 * This node always has one element child node, which represents the element
 * containing the "*ngFor" attribute. E.g.
 * <code>&lt;li class="item" *ngFor="let item of list"&gt;</code>
 *
 *
 * @author Vaadin Ltd
 */
public class ForTemplateNode extends AbstractControlTemplateNode {

    private final AbstractElementTemplateNode childNode;
    private final String loopVariable;
    private final String collectionVariable;

    /**
     * Creates a new for template node.
     *
     * @param parent
     *            the parent node of this node, can not be <code>null</code>
     * @param collectionVariable
     *            the name of the variable to loop through
     * @param loopVariable
     *            the name of the variable used inside the loop
     * @param childBuilder
     *            the template builder for the child node
     */
    public ForTemplateNode(AbstractElementTemplateNode parent,
            String collectionVariable, String loopVariable,
            ElementTemplateBuilder childBuilder) {
        super(parent);
        this.collectionVariable = collectionVariable;
        this.loopVariable = loopVariable;
        childNode = childBuilder.build(this);
    }

    @Override
    public int getChildCount() {
        return 1; // The element containing *ngFor
    }

    @Override
    public TemplateNode getChild(int index) {
        if (index != 0) {
            throw new IllegalArgumentException(
                    getClass().getName() + " only has 1 child");
        }

        return childNode;
    }

    private ModelList getModelList(StateNode modelNode) {
        StateNode stateNodeWithList = (StateNode) modelNode
                .getFeature(ModelMap.class).getValue(collectionVariable);
        if (stateNodeWithList == null) {
            throw new IllegalArgumentException("No model defined for the key '"
                    + collectionVariable + "'");
        }
        return stateNodeWithList.getFeature(ModelList.class);
    }

    @Override
    public int getGeneratedElementCount(StateNode templateStateNode) {
        return getModelList(templateStateNode).size();
    }

    @Override
    public Element getElement(int index, StateNode templateStateNode) {
        return childNode.getElement(0,
                getModelList(templateStateNode).get(index));
    }

    @Override
    public Element getParentElement(StateNode node) {
        TemplateNode parentTemplateNode = getParent().get();
        StateNode nodeWithList = node.getParent();
        StateNode nodeWithModel = nodeWithList.getParent();

        return parentTemplateNode.getElement(0, nodeWithModel);
    }

    @Override
    protected void populateJson(JsonObject json) {
        throw new UnsupportedOperationException(
                "To be implemented when needed");
    }

    /**
     * Gets the loop variable.
     * <p>
     * Only for testing purposes.
     *
     * @return the loop variable
     */
    protected String getLoopVariable() {
        return loopVariable;
    }

    /**
     * Gets the collection variable.
     * <p>
     * Only for testing purposes.
     *
     * @return the collection variable
     */
    protected String getCollectionVariable() {
        return collectionVariable;
    }
}
