<?xml version="1.0" encoding="UTF-8"?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses. -->
<!-- (c) Copyright IBM Corp. 2006 All Rights Reserved. -->
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
  xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
  xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
  xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
  xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0"
  xmlns:presentation="urn:oasis:names:tc:opendocument:xmlns:presentation:1.0"
  xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
  xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0"
  xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0"
  xmlns:math="http://www.w3.org/1998/Math/MathML"
  xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0"
  xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0"
  xmlns:dom="http://www.w3.org/2001/xml-events" xmlns:xforms="http://www.w3.org/2002/xforms"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:anim="urn:oasis:names:tc:opendocument:xmlns:animation:1.0"
  xmlns:smil="urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0"
  xmlns:prodtools="http://www.ibm.com/xmlns/prodtools"
  version="2.0"
  xmlns:ditamsg="http://dita-ot.sourceforge.net/ns/200704/ditamsg" 
  exclude-result-prefixes="xs ditamsg">  
  
  <!-- =========== TEMPLATES FOR CALCULATING NESTED TAGS 
      NOTE:SOME TAGS' NUMBER ARE MULTPLIED BY A NUMBER FOR FLAGGING STYLES.=========== -->
  
  <xsl:template name="set_align_value" as="attribute()">
    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]/@align = 'left'">
        <xsl:attribute name="text:style-name">left</xsl:attribute>
      </xsl:when>
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]/@align = 'right'">
        <xsl:attribute name="text:style-name">right</xsl:attribute>
      </xsl:when>
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]/@align = 'center'">
        <xsl:attribute name="text:style-name">center</xsl:attribute>
      </xsl:when>
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]/@align = 'justify'">
        <xsl:attribute name="text:style-name">justify</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="text:style-name">left</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="render_simpletable">
    <xsl:call-template name="create_simpletable"/>
  </xsl:template>
    
  <xsl:template name="render_table">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- =========== FUNCTIONS FOR RELATED-LINKS =========== -->
  <!-- same file or not -->
  <xsl:template name="check_file_location">
    <xsl:choose>
      <xsl:when test="@href and starts-with(@href,'#')">
        <xsl:value-of select="'true'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'false'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="format_href_value">
    <xsl:choose>
      <xsl:when test="@href and starts-with(@href,'#')">
        <xsl:choose>
          <!-- get element id -->
          <xsl:when test="contains(@href,'/')">
            <xsl:value-of select="concat('#', substring-after(@href,'/'))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="@href and contains(@href,'#')">
        <xsl:value-of select="substring-before(@href,'#')"/>
      </xsl:when>
      <xsl:when test="@href and not(@href='')">
        <xsl:value-of select="@href"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  <!-- create related links -->
  <xsl:template name="create_related_links">
    <xsl:param name="samefile"/>
    <xsl:param name="text"/>
    <xsl:param name="href-value"/>
    
    <xsl:choose>
      <xsl:when test="@href and not(@href='')">
        <text:a xlink:type="simple">
          <xsl:attribute name="xlink:href">
            <xsl:choose>
              <xsl:when test="$samefile='true'">
                <xsl:value-of select="$href-value"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:variable name="NORMAMLIZEDOUTPUT" select="translate($OUTPUTDIR, '\', '/')"/>
                <xsl:value-of select="concat($FILEREF, $NORMAMLIZEDOUTPUT, '/', $href-value)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:call-template name="gen-linktxt"/>
          <xsl:if test="contains(@class,' topic/link ')">
            <xsl:apply-templates select="*[contains(@class,' topic/desc ')]"/>
            <text:line-break/>
          </xsl:if>
        </text:a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="output-message">
          <xsl:with-param name="msgnum">028</xsl:with-param>
          <xsl:with-param name="msgsev">E</xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="gen-linktxt">
    <xsl:choose>
      <xsl:when test="contains(@class,' topic/xref ')">
        <xsl:choose>
          <xsl:when test="text() or *">
            <xsl:apply-templates/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="contains(@class,' topic/link ')">
        <xsl:choose>
          <xsl:when test="*[contains(@class,' topic/linktext ')]">
            <xsl:value-of select="*[contains(@class,' topic/linktext ')]"/>
          </xsl:when>
          <xsl:when test="text()">
            <xsl:value-of select="text()"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="*" mode="ditamsg:draft-comment-in-content">
    <xsl:call-template name="output-message">
      <xsl:with-param name="msgnum">040</xsl:with-param>
      <xsl:with-param name="msgsev">I</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="*" mode="ditamsg:required-cleanup-in-content">
    <xsl:call-template name="output-message">
      <xsl:with-param name="msgnum">039</xsl:with-param>
      <xsl:with-param name="msgsev">W</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
</xsl:stylesheet>