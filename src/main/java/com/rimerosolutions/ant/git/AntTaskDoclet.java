/*
 * Copyright 2013, Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rimerosolutions.ant.git;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

/**
 * Quick and very dirty doclet for Ant tasks documentation. Self-Contained for
 * now, no template engine or external dependencies.
 * 
 * @author Yves Zoundi
 * 
 */
public class AntTaskDoclet extends Standard {
        private static final String ANT_TASK_CLASS_NAME = "org.apache.tools.ant.Task";
        private static String destDir;
        private static String doctitle = "Ant tasks documentation";
        private static String header = "Ant tasks documentation";
        private static String windowtitle = "Ant tasks documentation";
        private static String bottom;

        public static final boolean start(RootDoc root) {
                readDestDirOption(root.options());
                writeContents(root.classes());
                return true;
        }

        public static void readDestDirOption(String options[][]) {
                for (int i = 0; i < options.length; i++) {
                        String[] opt = options[i];
                        if (opt[0].equals("-d")) {
                                destDir = opt[1];
                        } else if (opt[0].equals("-header")) {
                                header = opt[1];
                        } else if (opt[0].equals("-doctitle")) {
                                doctitle = opt[1];
                        } else if (opt[0].equals("-windowtitle")) {
                                windowtitle = opt[1];
                        } else if (opt[0].equals("-bottom")) {
                                bottom = opt[1];
                        }
                }
        }

        private static final List<String> ELEMENT_PREFIXES = Arrays.asList("create", "add", "addConfigured");
        private static final List<String> ATTRIBUTE_PREFIXES = Arrays.asList("set");

        private static Map<String, ClassData> classDataMap = new HashMap<String, ClassData>();

        private static String sanitizeName(String objectName, List<String> prefixes) {
                String newName = objectName;

                for (String prefix : prefixes) {
                        if (newName.startsWith(prefix)) {
                                String methodName = newName.substring(prefix.length() + 1);
                                return (("" + newName.charAt(prefix.length())).toLowerCase() + methodName).toLowerCase();
                        }
                }

                return newName;

        }

        private static class ElementData {
                String name;
                String description;

                static ElementData fromMethodDoc(MethodDoc methodDoc) {
                        ElementData data = new ElementData();

                        data.name = sanitizeName(methodDoc.name(), ELEMENT_PREFIXES);
                        data.description = methodDoc.commentText();

                        return data;
                }

                @Override
                public String toString() {
                        return name + ":" + description;
                }
        }

        private static class AttributeData {
                String name;
                String description;
                boolean required;

                static AttributeData fromMethodDoc(MethodDoc methodDoc) {
                        AttributeData data = new AttributeData();
                        data.description = methodDoc.commentText();
                        data.required = !(methodDoc.tags("antdoc.notrequired").length > 0);
                        data.name = sanitizeName(methodDoc.name(), ATTRIBUTE_PREFIXES);

                        return data;
                }

                @Override
                public String toString() {
                        return name + ", required:" + required + ", description:" + description;
                }
        }

        private static class ClassData {
                String qualifiedName;
                String simpleName;
                String description;
                List<String> parentClassDatas = new ArrayList<String>();
                List<AttributeData> attributesList = new ArrayList<AttributeData>();
                List<ElementData> elementsList = new ArrayList<ElementData>();
                boolean hidden;

                @Override
                public String toString() {
                        StringBuilder sb = new StringBuilder(512);

                        sb.append("------------------------\n");
                        sb.append(description);
                        sb.append("\n");
                        sb.append(qualifiedName);
                        sb.append("------------------------\n");

                        sb.append("parents:" + parentClassDatas + "\n");

                        sb.append("-> Elements:").append(elementsList).append("\n");

                        sb.append("-> Attributes:").append(attributesList).append("\n");

                        return sb.toString();
                }
        }

        static interface WriterCallback {
                void doWithWriter(Writer w) throws IOException;
        }

        static void withWriter(File f, WriterCallback wc) throws IOException {
                Writer w = null;

                try {
                        w = new OutputStreamWriter(new FileOutputStream(f));
                        wc.doWithWriter(w);
                } finally {
                        if (w != null) {
                                w.flush();
                                w.close();
                        }
                }
        }

        private static void writeIndexPage() {
                try {
                        withWriter(new File(new File(destDir), "index.html"), new WriterCallback() {

                                @Override
                                public void doWithWriter(Writer w) throws IOException {
                                        StringBuilder sb = new StringBuilder();

                                        sb.append("<html>");
                                        sb.append("<head><title>").append(windowtitle).append("</head></title>");
                                        sb.append("<frameset rows=\"15%,*\">");
                                        sb.append(" <frame src=\"header.html\">");
                                        sb.append(" <frameset cols=\"25%,75%\">");
                                        sb.append("<frame src=\"nav.html\">");
                                        sb.append("<frame name=\"bodycontents\" src=\"body.html\">");
                                        sb.append("</frameset></frameset></html>");
                                        sb.append("</html>");

                                        w.write(sb.toString());
                                }
                        });
                } catch (Exception e) {
                        System.exit(1);
                }
        }

        private static void writeHeaderPage() {
                try {
                        withWriter(new File(new File(destDir), "header.html"), new WriterCallback() {
                                @Override
                                public void doWithWriter(Writer w) throws IOException {
                                        StringBuilder sb = new StringBuilder();

                                        sb.append("<html><head><title>");
                                        sb.append(windowtitle).append("</title></head><body><h1>");
                                        sb.append(windowtitle);
                                        sb.append("</h1></body></html>");

                                        w.write(sb.toString());
                                }
                        });
                } catch (Exception e) {
                        System.exit(1);
                }
        }

        private static void writeBodyPage() {
                try {
                        withWriter(new File(new File(destDir), "body.html"), new WriterCallback() {
                                @Override
                                public void doWithWriter(Writer w) throws IOException {
                                        StringBuilder sb = new StringBuilder();

                                        sb.append("<html><head><title>");
                                        sb.append(windowtitle);
                                        sb.append("</title></head><body><div>");
                                        sb.append(doctitle);
                                        sb.append("</div></body></html>");

                                        w.write(sb.toString());
                                }
                        });
                } catch (Exception e) {
                        System.exit(1);
                }
        }

        private static void writeNavPage() {
                try {
                        File outputFile = new File(new File(destDir), "nav.html");
                        OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(outputFile));
                        StringBuilder sb = new StringBuilder();

                        sb.append("<html><head><title>Nav</title></head><body><div><ul>");

                        for (Map.Entry<String, ClassData> entry : classDataMap.entrySet()) {
                                if (!entry.getValue().hidden) {
                                        sb.append("<li>");
                                        sb.append("<a target=\"bodycontents\" href=\"");
                                        sb.append(entry.getKey()).append(".html");
                                        sb.append("\">");
                                        sb.append(entry.getValue().simpleName);
                                        sb.append("</a></li>");
                                }
                        }

                        sb.append("</ul></div></body></html>");
                        w.write(sb.toString());

                        w.flush();
                        w.close();
                } catch (Exception e) {
                        System.exit(1);
                }
        }

        private static void writeHtmlTasks() {
                try {
                        Map<String, ClassData> classDataCopy = new HashMap<String, AntTaskDoclet.ClassData>(classDataMap);

                        for (Map.Entry<String, ClassData> entry : classDataMap.entrySet()) {

                                String classDocName = entry.getKey();
                                ClassData classData = entry.getValue();

                                if (classData.hidden) {
                                        continue;
                                }

                                File outputFile = new File(new File(destDir), classDocName + ".html");

                                OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(outputFile));

                                StringBuilder header = new StringBuilder();

                                header.append("<html><head><title>classDocName</title><body><div>");
                                w.write(header.toString());

                                header = new StringBuilder();
                                header.append("<h1>").append(classData.simpleName).append("</h1>");
                                header.append("<hr/>");

                                header.append("<h2>Description</h2>");
                                header.append("<div>").append(classData.description).append("</div>");

                                w.write(header.toString());

                                List<AttributeData> attributesCopy = new ArrayList<AntTaskDoclet.AttributeData>(classData.attributesList);
                                List<ElementData> elementsCopy = new ArrayList<AntTaskDoclet.ElementData>(classData.elementsList);

                                if (!classData.parentClassDatas.isEmpty()) {
                                        for (String parent : classData.parentClassDatas) {
                                                attributesCopy.addAll(classDataCopy.get(parent).attributesList);
                                                elementsCopy.addAll(classDataCopy.get(parent).elementsList);
                                        }
                                }

                                if (!attributesCopy.isEmpty()) {
                                        StringBuilder sb = new StringBuilder();
                                        
                                        sb.append("<h2>Attributes</h2>");
                                        sb.append("<table border='1'>");
                                        sb.append("<tr>");
                                        sb.append("<th>Name</th>").append("<th>Description</th>").append("<th>Required</th>");
                                        sb.append("</tr>");
                                        
                                        for (AttributeData attr : attributesCopy) {
                                                sb.append("<tr>");
                                                sb.append("<td>").append(attr.name).append("</td>");
                                                sb.append("<td>").append(attr.description).append("</td>");
                                                sb.append("<td>").append(attr.required).append("</td>");
                                                sb.append("</tr>");
                                        }
                                        
                                        sb.append("</table>");

                                        w.write(sb.toString());
                                }

                                if (!elementsCopy.isEmpty()) {
                                        StringBuilder sb = new StringBuilder();
                                        
                                        sb.append("<h2>Nested elements</h2>");
                                        sb.append("<table border='1'>");
                                        sb.append("<tr>");
                                        sb.append("<th>Name</th>").append("<th>Description</th>");
                                        sb.append("</tr>");
                                        
                                        for (ElementData attr : elementsCopy) {
                                                sb.append("<tr>");
                                                sb.append("<td>").append(attr.name).append("</td>");
                                                sb.append("<td>").append(attr.description).append("</td>");
                                                sb.append("</tr>");
                                        }
                                        
                                        sb.append("</table>");

                                        w.write(sb.toString());
                                }

                                header = new StringBuilder();
                                header.append("<div><p>").append(bottom).append("</p></div>");
                                w.write(header.toString());

                                header = new StringBuilder();
                                header.append("</div></body></html>");
                                w.write(header.toString());

                                w.flush();
                                w.close();
                        }
                } catch (Exception e) {
                        System.exit(1);
                }
        }

        private static void writeHtmlToOutputDir() {
                writeBodyPage();
                writeNavPage();
                writeHeaderPage();
                writeIndexPage();
                writeHtmlTasks();

        }

        private static List<String> collectParentClassesNames(ClassDoc doc) {
                List<String> parents = new ArrayList<String>();

                ClassDoc currentParent = doc.superclass();

                while (currentParent != null) {
                        if (currentParent.qualifiedTypeName().equals(ANT_TASK_CLASS_NAME)) {
                                break;
                        }
                        parents.add(currentParent.qualifiedTypeName());
                        currentParent = currentParent.superclass();
                }

                return parents;
        }

        private static boolean isAntTask(ClassDoc classDoc) {
                ClassDoc currentParent = classDoc.superclass();

                while (currentParent != null) {
                        if (currentParent.qualifiedTypeName().equals(ANT_TASK_CLASS_NAME)) {
                                return true;
                        }

                        currentParent = currentParent.superclass();
                }

                return false;
        }

        private static void registerClassData(ClassDoc doc) {
                ClassData data = new ClassData();

                Scanner sc = new Scanner(doc.commentText());

                data.description = sc.nextLine();
                sc.close();
                data.qualifiedName = doc.qualifiedTypeName();
                data.simpleName = doc.name();
                data.hidden = doc.isAbstract();
                data.parentClassDatas.addAll(collectParentClassesNames(doc));

                for (MethodDoc methodDoc : doc.methods()) {
                        if (!isHiddenMethodDoc(methodDoc)) {
                                if (isTaskAttribute(methodDoc)) {
                                        data.attributesList.add(AttributeData.fromMethodDoc(methodDoc));
                                } else if (isTaskElement(methodDoc)) {
                                        data.elementsList.add(ElementData.fromMethodDoc(methodDoc));
                                }
                        }
                }

                classDataMap.put(data.qualifiedName, data);
        }

        private static boolean isHiddenMethodDoc(MethodDoc methodDoc) {
                return methodDoc.isPrivate() || methodDoc.isProtected() || methodDoc.isAbstract();
        }

        private static boolean namePrefixMatches(String name, List<String> prefixes) {
                for (String prefix : prefixes) {
                        if (name.startsWith(prefix)) {
                                return true;
                        }
                }

                return false;
        }

        private static boolean isTaskAttribute(MethodDoc methodDoc) {
                return namePrefixMatches(methodDoc.name(), ATTRIBUTE_PREFIXES);
        }

        private static boolean isTaskElement(MethodDoc methodDoc) {
                return namePrefixMatches(methodDoc.name(), ELEMENT_PREFIXES);
        }

        private static void writeContents(ClassDoc[] classDocs) {
                for (ClassDoc classDoc : classDocs) {
                        if (isAntTask(classDoc)) {
                                registerClassData(classDoc);
                        }
                }

                writeHtmlToOutputDir();
        }

        public static LanguageVersion languageVersion() {
                return LanguageVersion.JAVA_1_5;
        }

}
