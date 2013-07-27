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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

/**
 * Quick and very dirty doclet for Ant tasks documentation.
 * Self-Contained for now, no template engine or external dependencies.
 *
 * @author Yves Zoundi
 */
public final class AntTaskDoclet extends Standard {
        private static final String ENCODING_UTF_8 = "UTF-8";
        private static final String ANT_TASK_CLASS_NAME = "org.apache.tools.ant.Task";
        private static String destDir;
        private static String doctitle = "Ant tasks documentation";
        private static String header = "Ant tasks documentation";
        private static String windowtitle = "Ant tasks documentation";
        private static String bottom;
        private static String overview;
        private static final Logger LOG = Logger.getLogger(AntTaskDoclet.class.getName());
        private static final List<String> ELEMENT_PREFIXES = Arrays.asList("create", "add", "addConfigured");
        private static final List<String> ATTRIBUTE_PREFIXES = Arrays.asList("set");
        private static final String TAG_ANTDOC_NOTREQUIRED = "antdoc.notrequired";
        private static Map<String, ClassData> classDataMap = new HashMap<String, ClassData>();
        private static final String HTML_INDEX_PAGE = "index.html";
        private static final String HTML_HEADER_PAGE = "header.html";
        private static final String HTML_BODY_PAGE = "body.html";
        private static final String HTML_NAV_PAGE = "nav.html";

        private static final class ElementData {
                String name;
                String description;

                static ElementData fromMethodDoc(MethodDoc methodDoc) {
                        ElementData data = new ElementData();
                        data.name = sanitizeName(methodDoc.name(), ELEMENT_PREFIXES);
                        data.description = methodDoc.commentText();

                        return data;
                }
        }

        private static final class AttributeData {
                String name;
                String description;
                boolean required;

                static AttributeData fromMethodDoc(MethodDoc methodDoc) {
                        AttributeData data = new AttributeData();
                        data.description = methodDoc.commentText();
                        data.required = !(methodDoc.tags(TAG_ANTDOC_NOTREQUIRED).length > 0);
                        data.name = sanitizeName(methodDoc.name(), ATTRIBUTE_PREFIXES);

                        return data;
                }
        }

        private static final class ClassData {
                String qualifiedName;
                String simpleName;
                String description;
                List<String> parentClassDatas = new ArrayList<String>();
                List<AttributeData> attributesList = new ArrayList<AttributeData>();
                List<ElementData> elementsList = new ArrayList<ElementData>();
                boolean hidden;
        }

        static interface WriterCallback {
                void doWithWriter(Writer w) throws IOException;
        }

        private static void copyFile(File sourceFile, File destFile) throws IOException {
                InputStream in = null;
                OutputStream out = null;

                try {
                        in = new FileInputStream(sourceFile);
                        out = new FileOutputStream(destFile);
                        byte[] buf = new byte[4096];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                        }
                } finally {
                        try {
                                if (in != null) {
                                        in.close();
                                }
                        } finally {
                                if (out != null) {
                                        out.close();
                                }
                        }
                }
        }

        public static final boolean start(RootDoc root) {
                readDestDirOption(root.options());
                
                try {
                        writeContents(root.classes());
                } catch (IOException ioe) {
                        LOG.log(Level.SEVERE, "Ant tasks documentation failed", ioe);
                        return false;
                }

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
                        } else if (opt[0].equals("-overview")) {
                                overview = opt[1];
                        } else if (opt[0].equals("-bottom")) {
                                bottom = opt[1];
                        }
                }
        }

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

        static void withWriter(File f, WriterCallback wc) throws IOException {
                Writer w = null;

                try {
                        w = new OutputStreamWriter(new FileOutputStream(f), ENCODING_UTF_8);
                        wc.doWithWriter(w);
                } finally {
                        if (w != null) {
                                w.flush();
                                w.close();
                        }
                }
        }

        private static void writeIndexPage() throws IOException {
                withWriter(new File(new File(destDir), HTML_INDEX_PAGE), new WriterCallback() {

                                @Override
                                public void doWithWriter(Writer w) throws IOException {
                                        StringBuilder sb = new StringBuilder();

                                        sb.append("<html><head>");
                                        sb.append(htmlElement("title", windowtitle));
                                        sb.append("</head>");
                                        sb.append("<frameset rows=\"15%,*\">");
                                        sb.append(" <frame src=\"");
                                        sb.append(HTML_HEADER_PAGE);
                                        sb.append("\">");
                                        sb.append(" <frameset cols=\"25%,75%\">");
                                        sb.append("<frame src=\"");
                                        sb.append(HTML_NAV_PAGE);
                                        sb.append("\">");
                                        sb.append("<frame name=\"bodycontents\" src=\"");
                                        sb.append(HTML_BODY_PAGE);
                                        sb.append("\">");
                                        sb.append("</frameset></frameset></html>");
                                        sb.append("</html>");

                                        w.write(sb.toString());
                                }
                        });
        }

        private static void writeHeaderPage() throws IOException {
                withWriter(new File(new File(destDir), HTML_HEADER_PAGE), new WriterCallback() {
                                @Override
                                public void doWithWriter(Writer w) throws IOException {
                                        StringBuilder sb = new StringBuilder();

                                        sb.append("<html><head>");
                                        sb.append(htmlElement("title", windowtitle));
                                        sb.append("</head><body>");
                                        sb.append(htmlElement("h1", header));
                                        sb.append("</body></html>");

                                        w.write(sb.toString());
                                }
                        });
        }

        private static void writeBodyPage() throws IOException {
                if (overview == null) {
                        withWriter(new File(new File(destDir), HTML_BODY_PAGE), new WriterCallback() {
                                        @Override
                                        public void doWithWriter(Writer w) throws IOException {
                                                StringBuilder sb = new StringBuilder();

                                                sb.append("<html><head>");
                                                sb.append(htmlElement("title", windowtitle));
                                                sb.append("</head><body>");
                                                sb.append(htmlElement("div", doctitle));
                                                sb.append("</body></html>");

                                                w.write(sb.toString());
                                        }
                                });
                } else {
                        copyFile(new File(overview), new File(new File(destDir), HTML_BODY_PAGE));
                }
        }

        private static void writeNavPage() throws IOException {
                withWriter(new File(new File(destDir), HTML_NAV_PAGE), new WriterCallback() {
                                @Override
                                public void doWithWriter(Writer w) throws IOException {
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
                                }
                        });
        }

        private static String htmlElement(String tagName, Object contents) {
                return new StringBuilder().
                        append('<').
                        append(tagName).
                        append('>').
                        append(contents).
                        append("</").
                        append(tagName).
                        append('>').
                        toString();
        }

        private static void writeHtmlTasks() throws IOException {
                Map<String, ClassData> classDataCopy = new HashMap<String, AntTaskDoclet.ClassData>(classDataMap);

                for (Map.Entry<String, ClassData> entry : classDataMap.entrySet()) {
                        String classDocName = entry.getKey();
                        ClassData classData = entry.getValue();

                        if (classData.hidden) {
                                continue;
                        }

                        File outputFile = new File(new File(destDir), classDocName + ".html");
                        OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(outputFile), ENCODING_UTF_8);
                        StringBuilder header = new StringBuilder();

                        header.append("<html><head><title>classDocName</title><body><div>");
                        w.write(header.toString());

                        header = new StringBuilder();
                        header.append(htmlElement("h1", classData.simpleName));
                        header.append("<hr/>");

                        header.append(htmlElement("h2", "Description"));
                        header.append(htmlElement("div", classData.description));

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
                                        sb.append(htmlElement("td", attr.name));
                                        sb.append(htmlElement("td", attr.description));
                                        sb.append(htmlElement("td", attr.required));
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
                                        sb.append(htmlElement("td", attr.name));
                                        sb.append(htmlElement("td", attr.description));
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
        }

        private static void writeHtmlToOutputDir() throws IOException {
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

        private static void writeContents(ClassDoc[] classDocs) throws IOException {
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
