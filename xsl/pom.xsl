<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://maven.apache.org/POM/4.0.0"
                xmlns:m="http://maven.apache.org/POM/4.0.0"
                exclude-result-prefixes="m"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd"
                version="1.0">
  <xsl:param name="version" />

  <xsl:template match="/m:project/m:version">
    <xsl:copy>
      <xsl:value-of select="$version" />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="m:url">
    <xsl:copy>
      <xsl:value-of select="." />
    </xsl:copy>
    <description>Apache Ant Command Line Wrapper</description>
    <licenses>
      <license>
        <name>The Apache Software License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>https://github.com/rimerosolutions/ant-wrapper</url>
      <connection>scm:git:https://github.com/rimerosolutions/ant-wrapper.git</connection>
      <developerConnection>scm:git:https://github.com/rimerosolutions/ant-wrapper.git</developerConnection>
    </scm>
    <developers>
      <developer>
        <id>rimerosolutions</id>
        <name>Yves Zoundi</name>
        <email>rimerosolutions@gmail.com</email>
      </developer>
    </developers>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
