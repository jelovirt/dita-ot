<?xml version="1.0" encoding="UTF-8"?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses. -->
<!-- (c) Copyright IBM Corp. 2005, 2006 All Rights Reserved. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
  xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:anim="urn:oasis:names:tc:opendocument:xmlns:animation:1.0"
  xmlns:smil="urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0"
  xmlns:prodtools="http://www.ibm.com/xmlns/prodtools" 
  xmlns:styleUtils="org.dita.dost.util.StyleUtils"
  xmlns:ditamsg="http://dita-ot.sourceforge.net/ns/200704/ditamsg"
  exclude-result-prefixes="xs styleUtils ditamsg"
  version="2.0">
  
  <xsl:variable name="msgprefix">DOTX</xsl:variable>
  
  <!-- =========== "GLOBAL" DECLARATIONS =========== -->
  
  <!-- The document tree of filterfile returned by document($FILTERFILE,/)-->
  <xsl:variable name="FILTERFILEURL">
    <xsl:choose>
      <xsl:when test="not($FILTERFILE)"/> <!-- If no filterfile leave empty -->
      <xsl:when test="starts-with($FILTERFILE,'file:')">
        <xsl:value-of select="$FILTERFILE"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="starts-with($FILTERFILE,'/')">
            <xsl:text>file://</xsl:text><xsl:value-of select="$FILTERFILE"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>file:/</xsl:text><xsl:value-of select="$FILTERFILE"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="FILTERDOC" select="document($FILTERFILEURL,/)"/>
  
  <!-- Define a newline character -->
  <xsl:variable name="newline" select="'&#xA;'"/>
    
  <!-- these elements are never processed in a conventional presentation. can be overridden. -->
  <xsl:template match="*[contains(@class,' topic/no-topic-nesting ')]"/>
  
  <xsl:template match="*[contains(@class,' topic/topic ')]">
    <xsl:variable name="topicType">
      <xsl:call-template name="determineTopicType"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$topicType = 'topicChapter'">
        <xsl:call-template name="processTopicChapter"/>
      </xsl:when>
      <xsl:when test="$topicType = 'topicAppendix'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$topicType = 'topicPart'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$topicType = 'topicPreface'">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$topicType = 'topicNotices'">
        <!-- Suppressed in normal processing, since it goes at the beginning of the book. -->
        <!-- <xsl:call-template name="processTopicNotices"/> -->
      </xsl:when>
      <xsl:when test="$topicType = 'topicSimple'">
        <xsl:apply-templates/>
      </xsl:when>
      <!--BS: skipp abstract (copyright) from usual content. It will be processed from the front-matter-->
      <xsl:when test="$topicType = 'topicAbstract'"/>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/body ')]">
    <xsl:apply-templates select="preceding-sibling::*[contains(@class,' topic/abstract ')]" mode="outofline"/>
    <xsl:apply-templates select="preceding-sibling::*[contains(@class,' topic/shortdesc ')]" mode="outofline"/>
    <xsl:apply-templates select="following-sibling::*[contains(@class,' topic/related-links ')]" mode="prereqs"/>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/bodydiv ')]">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="*[contains(@class,' topic/topic ')]/*[contains(@class,' topic/title ')]">
    <xsl:variable name="depth" select="count(ancestor::*[contains(@class,' topic/topic ')])" as="xs:integer"/>
    <xsl:call-template name="block-title">
      <xsl:with-param name="depth" select="$depth"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="*[contains(@class,' topic/section ')]">
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:variable name="title" select="*[contains(@class, ' topic/title ')]"/>
    <xsl:apply-templates select="$title"/>
    <xsl:apply-templates select="node() except $title"/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>	
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/sectiondiv ')]">
    <text:span>
      <xsl:apply-templates/>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/section ') or
                         contains(@class,' topic/example ')]/
                          *[contains(@class,' topic/title ')]">
    <xsl:variable name="headCount" select="count(ancestor::*[contains(@class,' topic/topic ')]) + 1"/>
    <!-- Heading_20_2 -->
    <text:p>
      <!--xsl:attribute name="text:style-name" select="concat('Heading_20_' , $headCount)"/-->
      <text:span text:style-name="bold">
        <xsl:apply-templates/>
     </text:span>
    </text:p>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/example ')]">
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:variable name="title" select="*[contains(@class, ' topic/title ')]"/>
    <xsl:apply-templates select="$title"/>
    <xsl:apply-templates select="node() except $title"/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>	
  </xsl:template>

  <xsl:template match="*[contains(@class,' topic/example ')]/*[contains(@class,' topic/title ')]">
    <text:span text:style-name="bold">
      <xsl:apply-templates/>
    </text:span>
    <text:line-break/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/fig ')]">
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/figgroup ')]">
    <text:span>
      <text:span>
        <xsl:apply-templates select="." mode="start-add-odt-flags"/>
        <xsl:apply-templates/>
        <xsl:apply-templates select="." mode="end-add-odt-flags"/>
      </text:span>
    </text:span>
    <text:line-break/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/fig ')]/*[contains(@class,' topic/title ')]">
    <xsl:variable name="ancestorlang">
      <xsl:call-template name="getLowerCaseLang"/>
    </xsl:variable>
    <xsl:variable name="fig-count-actual" select="count(preceding::*[contains(@class,' topic/fig ')]/*[contains(@class,' topic/title ')]) + 1"/>
   
    <text:p>
     <text:span text:style-name="center">
       <text:span text:style-name="bold">
         <xsl:call-template name="getVariable">
           <xsl:with-param name="id" select="'Figure'"/>
         </xsl:call-template>
         <xsl:text> </xsl:text>
         <xsl:value-of select="$fig-count-actual"/>
         <xsl:text>. </xsl:text>
         <xsl:value-of select="."/>
       </text:span>
     </text:span>
    </text:p>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/fig ')]/*[contains(@class,' topic/desc ')]" priority="2">
    <text:span>
      <xsl:apply-templates/>
    </text:span>
    <text:line-break/>
  </xsl:template>  

  <!-- =========== block things ============ -->

  <xsl:attribute-set name="p">
    <xsl:attribute name="text:style-name">Standard</xsl:attribute>
  </xsl:attribute-set>

  <xsl:template match="*[contains(@class,' topic/p ')]" name="topic.p">
    <xsl:param name="prefix" as="node()*"/>
    <xsl:param name="contents" as="node()*">
      <xsl:apply-templates/>
    </xsl:param>
    <text:p xsl:use-attribute-sets="p">
      <xsl:copy-of select="$prefix"/>
      <xsl:copy-of select="$contents"/>
    </text:p>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/ph ')]">
    <text:span>
      <xsl:if test="ancestor::*[contains(@class, ' topic/thead ') or
                                contains(@class, ' topic/sthead ')]">
        <xsl:attribute name="text:style-name">bold</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="." mode="start-add-odt-flags"/>
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
      <xsl:apply-templates select="." mode="end-add-odt-flags"/>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/keyword ')]">
    <text:span>
      <xsl:apply-templates select="." mode="start-add-odt-flags">
        <xsl:with-param name="type" select="'keyword'"/>
      </xsl:apply-templates>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="end-add-odt-flags">
        <xsl:with-param name="type" select="'keyword'"/>
      </xsl:apply-templates>
    </text:span>
  </xsl:template>
  
  <!-- lines tag -->
  <xsl:template match="*[contains(@class,' topic/lines ')]">   
    <text:span>
      <xsl:apply-templates select="." mode="start-add-odt-flags"/>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="end-add-odt-flags"/>
      <text:line-break/>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/pre ')]">
    <xsl:apply-templates select="." mode="start-add-odt-imgrevflags"/>
    <text:p text:style-name="Code_Style_Paragraph">
      <text:span>
        <xsl:apply-templates select="." mode="add-odt-flagging"/>
        <xsl:apply-templates/>
      </text:span>
    </text:p>
    <xsl:apply-templates select="." mode="end-add-odt-imgrevflags"/>
  </xsl:template>

  <xsl:template match="*[contains(@class,' topic/q ')]">
    <text:span>
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:call-template name="create_q_content"/>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>
  
  <xsl:template name="create_q_content">
    <xsl:variable name="styles" as="xs:string*">
      <xsl:call-template name="get_style_name"/> 
    </xsl:variable>
    <xsl:variable name="style_name" select="string-join($styles, '')" as="xs:string?"/>
    <xsl:variable name="trueStyleName" select="styleUtils:getHiStyleName($style_name)"/>
    <text:span>
      <xsl:if test="$trueStyleName!=''">
        <xsl:attribute name="text:style-name">
          <xsl:value-of select="$trueStyleName"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'OpenQuote'"/>
      </xsl:call-template>
      <xsl:apply-templates/>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'CloseQuote'"/>
      </xsl:call-template>
    </text:span>
  </xsl:template>

  <!-- named template library -->

<xsl:template name="block-title">
  <xsl:param name="depth" as="xs:integer"/>
  <text:h text:outline-level="{$depth}" text:style-name="Heading_20_{$depth}">
    <xsl:call-template name="gen-bookmark">
      <xsl:with-param name="flag" select="0"/>
    </xsl:call-template>
    <xsl:apply-templates/>
    <xsl:call-template name="gen-bookmark">
      <xsl:with-param name="flag" select="1"/>
    </xsl:call-template>
  </text:h>
</xsl:template>
  
  
<!-- font-weight="bold" -->
<xsl:template name="inline-em">
  <text:span text:style-name="bold">
    <xsl:apply-templates/>
  </text:span>
</xsl:template>

<xsl:template name="block-pre">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*[contains(@class,' topic/lq ')]" name="topic.lq">
  <xsl:variable name="samefile">
    <xsl:choose>
      <xsl:when test="@href and starts-with(@href,'#')">
        <xsl:value-of select="'true'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'false'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="href-value">
    <xsl:choose>
      <xsl:when test="@href and starts-with(@href,'#')">
        <xsl:choose>
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
  </xsl:variable>
  <xsl:apply-templates select="." mode="start-add-odt-flags"/>
  <xsl:apply-templates/>
  <xsl:call-template name="create_lq_content">
    <xsl:with-param name="samefile" select="$samefile"/>
    <xsl:with-param name="href-value" select="$href-value"/>
  </xsl:call-template>
  <xsl:apply-templates select="." mode="end-add-odt-flags"/>
</xsl:template>

<xsl:template name="create_lq_content">
  <xsl:param name="samefile" select="''"/>
  <xsl:param name="href-value" select="''"/>
  
  <xsl:choose>
    <xsl:when test="@href and not(@href='')"> 
      <text:line-break/>
      <!-- Insert citation as link, use @href as-is -->
      <text:span text:style-name="right">
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
          <xsl:choose>
            <xsl:when test="@reftitle">
              <xsl:value-of select="@reftitle"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@href"/>
            </xsl:otherwise>
          </xsl:choose>
        </text:a>
      </text:span>
    </xsl:when>
    <xsl:when test="@reftitle and not(@reftitle='')">
      <text:line-break/>
      <!-- Insert citation text -->
      <text:span>
        <xsl:value-of select="@reftitle"/>
      </text:span>
    </xsl:when>
    <xsl:otherwise><!--nop - do nothing--></xsl:otherwise>
  </xsl:choose>
</xsl:template>
<!-- generate bookmark with parent's topic id -->
<xsl:template name="gen-bookmark">
  <xsl:param name="flag"/>
  <xsl:if test="parent::*[contains(@class, ' topic/topic ')]/@id">
    <xsl:variable name="id" select="parent::*[contains(@class, ' topic/topic ')]/@id"/>
    <xsl:choose>
      <!-- if $flag is 0 create bookmark-start -->
      <xsl:when test="$flag = 0">
        <text:bookmark-start text:name="{$id}">
        </text:bookmark-start>
      </xsl:when>
      <!-- otherwise create bookmark-end -->
      <xsl:otherwise>
        <text:bookmark-end text:name="{$id}">
        </text:bookmark-end>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
  
</xsl:template>

<xsl:template match="*[contains(@class,' topic/cite ')]">
  <text:span text:style-name="italic">
    <xsl:apply-templates/>
  </text:span>
</xsl:template>

<xsl:template match="*[contains(@class,' topic/desc ')]" priority="0">
  <text:span>
    <!-- 
    <xsl:apply-templates select="text()"/>
    -->
    <xsl:apply-templates/>
  </text:span>
  <!-- 
  <xsl:apply-templates select="*[@class]"/>
  -->
</xsl:template>

<xsl:template match="*[contains(@class,' topic/prolog ')]"/>
<xsl:template match="*[contains(@class,' topic/titlealts ')]"/>

<!-- Added for DITA 1.1 "Shortdesc proposal" -->
<xsl:template match="*[contains(@class,' topic/abstract ')]" mode="outofline">
  
  <text:p text:style-name="Default_20_Text">
    <xsl:apply-templates/>
  </text:p>
</xsl:template>

<xsl:template match="*[contains(@class,' topic/shortdesc ')]" mode="outofline">
  
  <xsl:choose>
    <xsl:when test="parent::*[contains(@class, ' topic/abstract ')]">
      <text:span>
        <xsl:apply-templates/>
      </text:span>
    </xsl:when>
    <xsl:otherwise>
      <text:p text:style-name="Default_20_Text">
          <xsl:apply-templates/>
      </text:p>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
  
<!-- Added for DITA 1.1 "Shortdesc proposal" -->
<xsl:template match="*[contains(@class,' topic/abstract ')]">
  <xsl:if test="not(following-sibling::*[contains(@class,' topic/body ')])">
    <xsl:apply-templates select="." mode="outofline"/>
    <xsl:apply-templates select="following-sibling::*[contains(@class,' topic/related-links ')]" mode="prereqs"/>
  </xsl:if>
</xsl:template>
  
<!-- Updated for DITA 1.1 "Shortdesc proposal" -->
<!-- Added for SF 1363055: Shortdesc disappears when optional body is removed -->
<xsl:template match="*[contains(@class,' topic/shortdesc ')]">
  <xsl:choose>
    <xsl:when test="parent::*[contains(@class, ' topic/abstract ')]">
      <xsl:apply-templates select="." mode="outofline.abstract"/>
    </xsl:when>
    <xsl:when test="not(following-sibling::*[contains(@class,' topic/body ')])">    
      <xsl:apply-templates select="." mode="outofline"/>
      <xsl:apply-templates select="following-sibling::*[contains(@class,' topic/related-links ')]" mode="prereqs"/>
    </xsl:when>
    <xsl:otherwise/>
  </xsl:choose>
</xsl:template>
  
  <!-- called shortdesc processing when it is in abstract -->
  <xsl:template match="*[contains(@class,' topic/shortdesc ')]" mode="outofline.abstract">
          <text:span>
            <xsl:apply-templates select="." mode="start-add-odt-flags"/>
            <xsl:apply-templates/>
            <xsl:apply-templates select="." mode="end-add-odt-flags"/>
          </text:span>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/note ')]" name="topic.note" priority="0">
    <xsl:apply-templates select="." mode="start-add-odt-flags">
      <xsl:with-param name="type" select="'note'"/>
    </xsl:apply-templates>
    <xsl:call-template name="create_note_content"/>
    <xsl:apply-templates select="." mode="end-add-odt-flags">
      <xsl:with-param name="type" select="'note'"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template name="create_note_content">
    <xsl:variable name="prefix" as="node()*">
      <xsl:apply-templates select="." mode="note-label"/>
    </xsl:variable>
    <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
    <xsl:choose>
      <xsl:when test="empty($prefix)">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="*[1][contains(@class, ' topic/p ')]">
        <xsl:apply-templates select="*[1]">
          <xsl:with-param name="prefix" select="$prefix"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="*[position() gt 1]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="topic.p">
          <xsl:with-param name="contents" select="$prefix"/>
        </xsl:call-template>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
  </xsl:template>
  
  <xsl:template match="*[@type = 'note' or empty(@type)]" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Note'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
    <xsl:if test="ancestor::*[contains(@class,' topic/table ') or contains(@class,' topic/simpletable ')]">
      <text:tab/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="*[@type = 'tip']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Tip'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[@type = 'fastpath']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Fastpath'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>

  <xsl:template match="*[@type = 'important']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Important'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[@type = 'remember']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Remember'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[@type = 'restriction']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Restriction'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[@type = 'attention']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Attention'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>

  <xsl:template match="*[@type = 'caution']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Caution'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>

  <xsl:template match="*[@type = 'danger']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Danger'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[@type = 'trouble']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'Trouble'"/>
      </xsl:call-template>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[@type = 'other']" mode="note-label">
    <text:span text:style-name="bold">
      <xsl:choose>
        <xsl:when test="@othertype and not(@othertype = '')">
          <xsl:value-of select="@othertype"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>[other]</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="getVariable">
        <xsl:with-param name="id" select="'ColonSymbol'"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*" mode="note-label" priority="-10"/>

<xsl:template name="gen-txt">
  <xsl:param name="txt"/>
  <xsl:analyze-string select="." regex="\n">
    <xsl:matching-substring>
      <text:line-break/>
    </xsl:matching-substring>
    <xsl:non-matching-substring>
      <xsl:analyze-string select="." regex="\s+">
        <xsl:matching-substring>
          <xsl:choose>
            <xsl:when test="string-length(.) eq 1">
              <xsl:text> </xsl:text>    
            </xsl:when>
            <xsl:otherwise>
              <text:s text:c="{string-length(.)}"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:matching-substring>
        <xsl:non-matching-substring>
          <xsl:value-of select="."/>
        </xsl:non-matching-substring>
      </xsl:analyze-string>
    </xsl:non-matching-substring>
  </xsl:analyze-string>
</xsl:template>

<xsl:template match="text()">
  <xsl:variable name="styles" as="xs:string*">
    <xsl:call-template name="get_style_name"/> 
  </xsl:variable>
  <xsl:variable name="style_name" select="string-join($styles, '')" as="xs:string?"/>
  <xsl:variable name="trueStyleName" select="styleUtils:getHiStyleName($style_name)"/>
  <text:span>
    <xsl:if test="$trueStyleName!=''">
      <xsl:choose>
        <xsl:when test="ancestor::*[contains(@class, ' topic/title ')][1]/parent::*[contains(@class, ' topic/topic ')]">
          <xsl:choose>
            <xsl:when test="$trueStyleName = 'bold'"/>
            <xsl:otherwise>
              <xsl:attribute name="text:style-name">
                <xsl:value-of select="$trueStyleName"/>
              </xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="text:style-name">
            <xsl:value-of select="$trueStyleName"/>
          </xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="ancestor::*[contains(@class,' topic/pre ')]">
        <xsl:call-template name="gen-txt">
          <xsl:with-param name="txt" select="."/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="t" select="normalize-space(.)"/>
        <xsl:if test="not($t = '')">
          <xsl:if test="starts-with(.,' ')">
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:value-of select="$t"/>
          <xsl:if test="substring(., string-length(.)) = ' '">
            <xsl:text> </xsl:text>
          </xsl:if>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </text:span>
</xsl:template>

<xsl:template match="*[contains(@class,' topic/boolean ')]">
  <xsl:variable name="styles" as="xs:string*">
    <xsl:call-template name="get_style_name"/> 
  </xsl:variable>
  <xsl:variable name="style_name" select="string-join($styles, '')" as="xs:string?"/>
  <xsl:variable name="trueStyleName" select="styleUtils:getHiStyleName($style_name)"/>
  <text:span>
    <xsl:if test="$trueStyleName != ''">
      <xsl:attribute name="text:style-name" select="concat('boolean_', $trueStyleName)"/>
    </xsl:if>
    <text:span text:style-name="boolean_style">
      <xsl:value-of select="name()"/>
     <xsl:text>:</xsl:text>
     <xsl:value-of select="@state"/>
    </text:span>
  </text:span>
</xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/state ')]">  
    <xsl:variable name="styles" as="xs:string*">
      <xsl:call-template name="get_style_name"/> 
    </xsl:variable>
    <xsl:variable name="style_name" select="string-join($styles, '')" as="xs:string?"/>
    <xsl:variable name="trueStyleName" select="styleUtils:getHiStyleName($style_name)"/>
    <text:span>
      <xsl:if test="$trueStyleName!=''">
        <xsl:attribute name="text:style-name">
          <xsl:value-of select="$trueStyleName"/>
        </xsl:attribute>
      </xsl:if>
      <text:span text:style-name="state_style">
        <xsl:value-of select="name()"/>
        <xsl:text>: </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>=</xsl:text>
        <xsl:value-of select="@value"/>
      </text:span>
    </text:span>
  </xsl:template>
  
<!-- itemgroup tag -->
<xsl:template match="*[contains(@class, ' topic/itemgroup ')]">
  <xsl:apply-templates select="." mode="start-add-odt-flags"/>
  <xsl:apply-templates/>
  <xsl:apply-templates select="." mode="end-add-odt-flags"/>	
</xsl:template>

<!-- Indexterm tag -->
<xsl:template match="*[contains(@class, ' topic/indexterm ')]">
  <xsl:if test="$INDEXSHOW='yes'">
    <xsl:variable name="indexId" select="concat('IMark', generate-id(.))"/>
    <xsl:variable name="depth" select="count(ancestor-or-self::*[contains(@class,' topic/indexterm ')])"/>
    <text:user-index-mark-start text:id="{$indexId}" text:index-name="{'user-defined'}" text:outline-level="{$depth}"/>
    <xsl:apply-templates/>
    <text:user-index-mark-end text:id="{$indexId}"/>   
  </xsl:if>
</xsl:template>

  <xsl:template match="*[contains(@class,' topic/tm ')]">
    <xsl:call-template name="create_tm_content"/>
  </xsl:template>
  
  <xsl:template name="create_tm_content">
    <text:span>
      <xsl:apply-templates/>
    </text:span>
    <text:span text:style-name="sup">
      <xsl:choose>
        <xsl:when test="@tmtype='tm'">
          <xsl:text>&#x2122;</xsl:text>
        </xsl:when>
        <xsl:when test="@tmtype='service'">
          <xsl:text>&#x2120;</xsl:text>
        </xsl:when>
        <xsl:when test="@tmtype='reg'">
          <xsl:text>&#xAE;</xsl:text>
        </xsl:when>
      </xsl:choose>
    </text:span>
  </xsl:template>

<xsl:template match="*[contains(@class,' topic/draft-comment ')]">
  <xsl:apply-templates select="." mode="start-add-odt-flags"/>
  <text:span text:style-name="bold">
    <xsl:call-template name="getVariable">
      <xsl:with-param name="id" select="'Draft comment'"/>
    </xsl:call-template>
    <xsl:call-template name="getVariable">
      <xsl:with-param name="id" select="'ColonSymbol'"/>
    </xsl:call-template>
    <xsl:if test="@author">
      <xsl:value-of select="@author"/><xsl:text> </xsl:text>
    </xsl:if>
    <xsl:if test="@disposition">
      <xsl:value-of select="@disposition"/><xsl:text> </xsl:text>
    </xsl:if>
    <xsl:if test="@time">
      <xsl:value-of select="@time"/>
    </xsl:if>
    <text:line-break/>
  </text:span>
  <xsl:apply-templates/>
  <xsl:apply-templates select="." mode="end-add-odt-flags"/>
</xsl:template>
  
<xsl:template match="*[contains(@class,' topic/required-cleanup ')]">
  <xsl:if test="$DRAFT='yes'">
    <xsl:apply-templates select="." mode="ditamsg:required-cleanup-in-content"/>
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <!-- 
    <xsl:element name="text:span">
      <xsl:attribute name="text:style-name">required_cleanup_style</xsl:attribute>
    -->
      <text:span text:style-name="bold">
        <xsl:text>[</xsl:text>
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'Required cleanup'"/>
        </xsl:call-template>
        <xsl:text>]</xsl:text>
        <xsl:if test="string(@remap)">
          <xsl:text>(</xsl:text>
          <xsl:value-of select="@remap"/>
          <xsl:text>)</xsl:text>
        </xsl:if>
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'ColonSymbol'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
      </text:span>
      <xsl:apply-templates/>
    <!-- 
    </xsl:element>
    -->
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>
  </xsl:if>
</xsl:template>

  <!-- FIXME: prolog, why output? -->
  <xsl:template match="*[contains(@class, ' topic/keywords ')]">
    <text:p xsl:use-attribute-sets="p">
      <xsl:apply-templates/>
    </text:p>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/term ')]">      
    <text:span text:style-name="italic">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' topic/fn ')]">
    <xsl:choose>
      <xsl:when test="not(@id)">
        <xsl:choose>
          <xsl:when test="@callout and not(@callout = '')">
            <text:note text:note-class="footnote">
              <text:note-citation text:label="{@callout}">
                <xsl:value-of select="@callout"/>
              </text:note-citation>
              <text:note-body>
                <text:p text:style-name="footnote">
                  <text:span>
                    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
                    <xsl:apply-templates/>
                    <xsl:apply-templates select="." mode="end-add-odt-flags"/>
                  </text:span>
                </text:p>
              </text:note-body>
            </text:note>
          </xsl:when>
          <xsl:otherwise>
            <!-- should be updated -->
            <!-- 
              <xsl:variable name="fnNumber" 
              select="count(preceding::*[contains(@class, ' topic/fn ')]) + 1"/>
            -->
            <xsl:variable name="fnNumber">
              <xsl:number from="/" level="any"/>
            </xsl:variable>
            <text:note text:note-class="footnote">
              <text:note-citation>
                <xsl:value-of select="$fnNumber"/>
              </text:note-citation>
              <text:note-body>
                <text:p text:style-name="footnote">
                  <text:span>
                    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
                    <xsl:apply-templates/>
                    <xsl:apply-templates select="." mode="end-add-odt-flags"/>
                  </text:span>
                </text:p>
              </text:note-body>
            </text:note>  
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>  
  </xsl:template>
  
  <!-- Add for "New <data> element (#9)" in DITA 1.1 -->
  <xsl:template match="*[contains(@class,' topic/data ')]"/>

  <!-- data-about tag -->
  <xsl:template match="*[contains(@class, ' topic/data-about ')]">
    <text:span>
      <xsl:apply-templates/>
    </text:span>
  </xsl:template>
  
  <!-- Add for "Support foreign content vocabularies such as 
  MathML and SVG with <unknown> (#35) " in DITA 1.1 -->
  <xsl:template match="*[contains(@class,' topic/foreign ') or contains(@class,' topic/unknown ')]"/>

</xsl:stylesheet>