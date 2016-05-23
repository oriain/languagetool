package org.languagetool.tagging.it;

import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by littl on 5/21/2016.
 */
public class CoNLL {

    public static void writeFile(ConcurrentDependencyGraph graph, String filepath) throws FileNotFoundException {
        // Output the CoNLL file for easier viewing.
        PrintWriter out = new PrintWriter(filepath);
        for (int i = 1; i<graph.nTokenNodes(); i++) {
            ConcurrentDependencyNode tokenNode = graph.getTokenNode(i);
            out.print(tokenNode.getLabel(0));
            for (int j=1; j<10; j++){
                out.print("\t");
                if (j==6) out.print(tokenNode.getHeadIndex());
                else out.print(tokenNode.getLabel(j));
            }
            out.println();
        }
        out.close();
    }
}
