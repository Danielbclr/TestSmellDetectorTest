package com.test.aadetector;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class CodeBlockExtractor {

    public static void extractBlocks(String testClassPath) {
//        String testClassPath = "path/to/YourTestClass.java";
        List<String> ifBlocks = new ArrayList<>();
        List<String> forBlocks = new ArrayList<>();
        List<String> whileBlocks = new ArrayList<>();
        List<String> otherBlocks = new ArrayList<>();

        getCodeBlocks(testClassPath, ifBlocks, forBlocks, whileBlocks, otherBlocks);

        System.out.println("IF BLOCKS:");
        for (String ifBlock : ifBlocks) {
            System.out.println(ifBlock);
            System.out.println("-----");
        }

        System.out.println("FOR BLOCKS:");
        for (String forBlock : forBlocks) {
            System.out.println(forBlock);
            System.out.println("-----");
        }

        System.out.println("WHILE BLOCKS:");
        for (String whileBlock : whileBlocks) {
            System.out.println(whileBlock);
            System.out.println("-----");
        }

        System.out.println("OTHER BLOCKS:");
        for (String otherBlock : otherBlocks) {
            System.out.println(otherBlock);
            System.out.println("-----");
        }
    }

    public static void getCodeBlocks(String testClassPath, List<String> ifBlocks, List<String> forBlocks, List<String> whileBlocks, List<String> otherBlocks) {
        try {
            FileInputStream fileInputStream = new FileInputStream(testClassPath);
            JavaParser javaParser = new JavaParser();
            CompilationUnit compilationUnit = javaParser.parse(fileInputStream).getResult().get();

            for (Node node : compilationUnit.getChildNodes()) {
                findCodeBlocks(node, ifBlocks, forBlocks, whileBlocks, otherBlocks);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findCodeBlocks(Node node, List<String> ifBlocks, List<String> forBlocks, List<String> whileBlocks, List<String> otherBlocks) {
        if (node instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) node;
            ifBlocks.add(ifStmt.getThenStmt().toString());
            if (ifStmt.getElseStmt().isPresent()) {
                ifBlocks.add(ifStmt.getElseStmt().get().toString());
            }
        } else if (node instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) node;
            forBlocks.add(forStmt.getBody().toString());
        } else if (node instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) node;
            whileBlocks.add(whileStmt.getBody().toString());
        } else if (node instanceof BlockStmt) {
            if (!ifBlocks.contains(node.toString()) && !forBlocks.contains(node.toString()) && !whileBlocks.contains(node.toString())) {
                otherBlocks.add(node.toString());
            }
        }

        for (Node childNode : node.getChildNodes()) {
            findCodeBlocks(childNode, ifBlocks, forBlocks, whileBlocks, otherBlocks);
        }
    }
}
