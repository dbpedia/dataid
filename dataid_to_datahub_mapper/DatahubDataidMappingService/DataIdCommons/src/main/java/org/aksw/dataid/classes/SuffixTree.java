package org.aksw.dataid.classes;
/* The authors of this work have released all rights to it and placed it
in the public domain under the Creative Commons CC0 1.0 waiver
(http://creativecommons.org/publicdomain/zero/1.0/).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Suffix_tree_(Java)?oldid=18684
*/


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class AbstractSuffixTree {

    String text = null;
    SuffixTreeNode root = null;
    int inputAlphabetSize = -1;

    AbstractSuffixTree(String text) {
        if (text.length() > 0 && text.charAt(text.length() - 1) == '$') {
            this.text = text;
        } else {
            this.text = text + "$";
        }
    }
}
class SimpleSuffixTree extends AbstractSuffixTree {

    public SimpleSuffixTree(String text) {
        super(text);
        constructTree();
    }

    private void constructTree() {
        super.root = new SuffixTreeNode();
        char[] s = super.text.toCharArray();
        for (int i = 0; i < s.length; i++) {
            List<String> suffixList = new ArrayList<String>();
            for (int k = i; k < s.length; k++) {
                suffixList.add(s[k] + "");
            }
            super.root.addSuffix(suffixList, i+1);
        }
    }
}
class CompactSuffixTree extends AbstractSuffixTree {

    public CompactSuffixTree(SimpleSuffixTree simpleSuffixTree) {
        super(simpleSuffixTree.text);
        super.root = compactNodes(simpleSuffixTree.root, 0);
    }

    private SuffixTreeNode compactNodes(SuffixTreeNode node, int nodeDepth) {
        node.nodeDepth = nodeDepth;
        for (SuffixTreeNode child : node.children) {
            while (child.children.size() == 1) {
                SuffixTreeNode grandchild = child.children.iterator().next();
                child.incomingEdge.label += ", " + grandchild.incomingEdge.label;
                child.stringDepth += grandchild.incomingEdge.label.length();
                child.children = grandchild.children;
                for (SuffixTreeNode gc : child.children)
                    gc.parent = node;
            }
            child = compactNodes(child, nodeDepth + 1);
        }
        return node;
    }
}
class SuffixTreeNode {

    SuffixTreeEdge incomingEdge = null;
    int nodeDepth = -1;
    int label = -1;
    Collection<SuffixTreeNode> children = null;
    SuffixTreeNode parent = null;
    int stringDepth;
    int id = 0;
    public static int c;

    public SuffixTreeNode(SuffixTreeNode parent, String incomingLabel,
                          int depth, int label, int id) {
        children = new ArrayList<SuffixTreeNode>();
        incomingEdge = new SuffixTreeEdge(incomingLabel, label);
        nodeDepth = depth;
        this.label = label;
        this.parent = parent;
        stringDepth = parent.stringDepth + incomingLabel.length();
        this.id = id;
    }
    public SuffixTreeNode() {
        children = new ArrayList<SuffixTreeNode>();
        nodeDepth = 0;
        label = 0;
    }

    public void addSuffix(List<String> suffix, int pathIndex) {
        SuffixTreeNode insertAt = this;
        insertAt = search(this, suffix);
        insert(insertAt, suffix, pathIndex);
    }

    private SuffixTreeNode search(SuffixTreeNode startNode, List<String> suffix) {
        if (suffix.isEmpty()) {
            throw new IllegalArgumentException(
                    "Empty suffix. Probably no valid simple suffix tree exists for the input.");
        }
        Collection<SuffixTreeNode> children = startNode.children;
        for (SuffixTreeNode child : children) {
            if (child.incomingEdge.label.equals(suffix.get(0))) {
                suffix.remove(0);
                if (suffix.isEmpty()) {
                    return child;
                }
                return search(child, suffix);
            }
        }
        return startNode;
    }

    private void insert(SuffixTreeNode insertAt, List<String> suffix,
                        int pathIndex) {
        for (String x : suffix) {
            SuffixTreeNode child = new SuffixTreeNode(insertAt, x,
                    insertAt.nodeDepth + 1, pathIndex, id);
            insertAt.children.add(child);
            insertAt = child;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String incomingLabel = this.isRoot() ? "" : this.incomingEdge.label;
        for (int i = 1; i <= this.nodeDepth; i++)
            result.append("\t");
        if (this.isRoot()) {
            c = 1;
            this.id = 1;
        } else {
            this.id = c;
            result.append(this.parent.id + " -> ");
            result.append(this.id + "[label=\"" + incomingLabel + "\"];\n");
        }
        for (SuffixTreeNode child : children) {
            c++;
            child.id = c;
            result.append(child.toString());
        }
        return result.toString();
    }


    public boolean isRoot() {
        return this.parent == null;
    }
    public boolean isLeaf() {
        return children.size() == 0;
    }
}
class SuffixTreeEdge {

    String label = null;
    @SuppressWarnings("unused")
    private int branchIndex = -1;

    public SuffixTreeEdge(String label, int branchIndex) {
        this.label = label;
        this.branchIndex = branchIndex;
    }
}
