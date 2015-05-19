<?xml version="1.0" encoding="UTF-8"?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses. -->
<!-- (c) Copyright IBM Corp. 2005, 2006 All Rights Reserved. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0"
                version="2.0">

  <xsl:variable name="msgprefix">DOTX</xsl:variable>
  
  <xsl:param name="job-url"/>
  <xsl:variable name="job" select="document($job-url)"/>
  
  <xsl:template match="/">
    <manifest:manifest manifest:version="1.2">
      <xsl:call-template name="root"/>
      <xsl:call-template name="images"/>
    </manifest:manifest>
  </xsl:template>

  <xsl:template name="root">
    <!--manifest:file-entry manifest:full-path="settings.xml" manifest:media-type="text/xml"/-->
    <!--manifest:file-entry manifest:full-path="meta.xml" manifest:media-type="text/xml"/-->
    <manifest:file-entry manifest:full-path="manifest.rdf" manifest:media-type="application/rdf+xml"/>
    <!--manifest:file-entry manifest:full-path="Configurations2/accelerator/current.xml" manifest:media-type=""/-->
    <!--manifest:file-entry manifest:full-path="Configurations2/" manifest:media-type="application/vnd.sun.xml.ui.configuration"/-->
    <manifest:file-entry manifest:media-type="application/vnd.oasis.opendocument.text" manifest:full-path="/"/>
    <manifest:file-entry manifest:media-type="text/xml" manifest:full-path="content.xml"/>
    <manifest:file-entry manifest:media-type="text/xml" manifest:full-path="styles.xml"/>
  </xsl:template>
  
  <xsl:template name="images">
    <xsl:for-each select="$job/job/files/file[@format = 'image']">
      <manifest:file-entry manifest:media-type="image/jpeg" manifest:full-path="{@path}">
        <xsl:attribute name="manifest:media-type">
          <xsl:variable name="path" select="lower-case(@path)"/>
          <xsl:choose>
            <xsl:when test="ends-with($path, '.jpg') or ends-with($path, '.jpeg')">image/jpeg</xsl:when>
            <xsl:when test="ends-with($path, '.git')">image/git</xsl:when>
            <xsl:when test="ends-with($path, '.svg')">image/svg+xml</xsl:when>
            <xsl:when test="ends-with($path, '.png')">image/png</xsl:when>
            <xsl:when test="ends-with($path, '.mml')">application/mathml+xml</xsl:when>
          </xsl:choose>
        </xsl:attribute>
      </manifest:file-entry>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
