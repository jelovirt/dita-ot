<?xml version="1.0" encoding="UTF-8"?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses. -->
<!-- (c) Copyright IBM Corp. 2009, 2010 All Rights Reserved. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:dita-ot="http://dita-ot.sourceforge.net/ns/201007/dita-ot"
    exclude-result-prefixes="xs dita-ot"
    version="2.0">
    
    <xsl:template name="determineTopicType">
        <xsl:variable name="id" select="ancestor-or-self::*[contains(@class, ' topic/topic ')][1]/@id"/>
        <xsl:variable name="gid" select="generate-id(ancestor-or-self::*[contains(@class, ' topic/topic ')][1])"/>
        <xsl:variable name="topicNumber" select="count($topicNumbers/topic[@id = $id][following-sibling::topic[@guid = $gid]]) + 1"/>
        <xsl:variable name="mapTopic">
            <xsl:copy-of select="$map//*[@id = $id]"/>
        </xsl:variable>
        <xsl:variable name="foundTopicType">
            <xsl:apply-templates select="$mapTopic/*[position() = $topicNumber]" mode="determineTopicType"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$foundTopicType!=''"><xsl:value-of select="$foundTopicType"/></xsl:when>
            <xsl:otherwise>topicSimple</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="*" mode="determineTopicType">
        <!-- Default, when not matching a bookmap type, is topicSimple -->
        <xsl:text>topicSimple</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/chapter ')]" mode="determineTopicType">
        <xsl:text>topicChapter</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/appendix ')]" mode="determineTopicType">
        <xsl:text>topicAppendix</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/preface ')]" mode="determineTopicType">
        <xsl:text>topicPreface</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/part ')]" mode="determineTopicType">
        <xsl:text>topicPart</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/abbrevlist ')]" mode="determineTopicType">
        <xsl:text>topicAbbrevList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/bibliolist ')]" mode="determineTopicType">
        <xsl:text>topicBiblioList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/booklist ')]" mode="determineTopicType">
        <xsl:text>topicBookList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/figurelist ')]" mode="determineTopicType">
        <xsl:text>topicFigureList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/indexlist ')]" mode="determineTopicType">
        <xsl:text>topicIndexList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/toc ')]" mode="determineTopicType">
        <xsl:text>topicTocList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/glossarylist ')]" mode="determineTopicType">
        <xsl:text>topicGlossaryList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/trademarklist ')]" mode="determineTopicType">
        <xsl:text>topicTradeMarkList</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/notices ')]" mode="determineTopicType">
        <xsl:text>topicNotices</xsl:text>
    </xsl:template>
    <xsl:template match="*[contains(@class, ' bookmap/bookabstract ')]" mode="determineTopicType">
        <xsl:text>topicAbstract</xsl:text>
    </xsl:template>
    
    
    <!--  Bookmap Chapter processing  -->
    <xsl:template name="processTopicChapter">
        <xsl:apply-templates/>
        <!-- 
        <text:p text:style-name="PB"/>
        
            <xsl:call-template name="startPageNumbering"/>
            <xsl:call-template name="insertBodyStaticContents"/>
            
                <fo:block xsl:use-attribute-sets="topic">
                    <xsl:attribute name="id">
                        <xsl:value-of select="@id"/>
                    </xsl:attribute>
                    <xsl:if test="not(ancestor::*[contains(@class, ' topic/topic ')])">
                        <fo:marker marker-class-name="current-topic-number">
                            <xsl:number format="1"/>
                        </fo:marker>
                        <fo:marker marker-class-name="current-header">
                            <xsl:for-each select="*[contains(@class,' topic/title ')]">
                                <xsl:call-template name="getTitle"/>
                            </xsl:for-each>
                        </fo:marker>
                    </xsl:if>
                    
                    <xsl:apply-templates select="*[contains(@class,' topic/prolog ')]"/>
                    
                    <xsl:call-template name="insertChapterFirstpageStaticContent">
                        <xsl:with-param name="type" select="'chapter'"/>
                    </xsl:call-template>
                    
                    
                    <xsl:apply-templates select="*[contains(@class,' topic/topic ')]"/>
                </fo:block>
        -->
    </xsl:template>
  
  <!-- Flatten templates -->
  
  <!-- Flatten -->
  
  <!-- Check whether element is a container block -->
  <xsl:function name="dita-ot:is-container-block" as="xs:boolean">
    <xsl:param name="element" as="node()"/>
    <xsl:variable name="class" select="$element/@class" as="xs:string?"/>
    <xsl:choose>
    <xsl:when test="exists($class)">
      <xsl:sequence select="contains($class, ' topic/body ') or
                            (:contains($class, ' topic/pre ') or:)
                            contains($class, ' topic/note ') or
                            contains($class, ' topic/fig ') or
                            contains($class, ' topic/li ') or
                            contains($class, ' topic/sli ') or
                            contains($class, ' topic/dt ') or
                            contains($class, ' topic/dd ') or
                            contains($class, ' topic/itemgroup ') or
                            contains($class, ' topic/draft-comment ') or
                            contains($class, ' topic/section ') or
                            contains($class, ' topic/entry ') or
                            contains($class, ' topic/stentry ') or
                            contains($class, ' topic/example ')"/>
      <!--
      contains($class, ' topic/p ') or
      contains($class, ' topic/table ') or
      contains($class, ' topic/simpletable ') or
      contains($class, ' topic/dl ') or
      contains($class, ' topic/sl ') or
      contains($class, ' topic/ol ') or
      contains($class, ' topic/ul ') or
    -->
    </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  
  <!-- Check whether element is a container or leaf block -->
  <xsl:function name="dita-ot:is-block" as="xs:boolean">
    <xsl:param name="element" as="node()"/>
    <xsl:variable name="class" select="$element/@class" as="xs:string?"/>
    <xsl:choose>
    <xsl:when test="exists($class)">
      <xsl:sequence select="contains($class, ' topic/body ') or
                            contains($class, ' topic/title ') or
                            contains($class, ' topic/section ') or 
                            contains($class, ' task/info ') or
                            contains($class, ' topic/p ') or
                            (contains($class, ' topic/image ') and $element/@placement = 'break') or
                            contains($class, ' topic/pre ') or
                            contains($class, ' topic/note ') or
                            contains($class, ' topic/fig ') or
                            contains($class, ' topic/dl ') or
                            contains($class, ' topic/sl ') or
                            contains($class, ' topic/ol ') or
                            contains($class, ' topic/ul ') or
                            contains($class, ' topic/li ') or
                            contains($class, ' topic/sli ') or
                            contains($class, ' topic/itemgroup ') or
                            contains($class, ' topic/section ') or
                            contains($class, ' topic/table ') or
                            contains($class, ' topic/entry ') or
                            contains($class, ' topic/simpletable ') or
                            contains($class, ' topic/stentry ') or
                            contains($class, ' topic/example ') or
                            contains($class, ' task/cmd ')"/>
    </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  
  
  <xsl:template match="@* | node()" mode="flatten" priority="-1000">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" mode="flatten"/>
    </xsl:copy>
  </xsl:template>
  
  
  <!--xsl:template match="*[contains(@class, ' task/step ') or
                         contains(@class, ' task/substep ')]" mode="flatten" priority="100">
    <xsl:copy>
      <xsl:apply-templates select="@* | *" mode="flatten"/>
    </xsl:copy>
  </xsl:template-->
  
  <xsl:template match="*[contains(@class, ' topic/p ')]" mode="flatten" priority="100">
    <xsl:choose>
      <xsl:when test="empty(node())"/>
      <xsl:when test="count(*) eq 1 and
                      (*[dita-ot:is-container-block(.)]) and 
                      empty(text()[normalize-space(.)])">
        <xsl:apply-templates mode="flatten"/>
      </xsl:when>
      <xsl:when test="descendant::*[dita-ot:is-block(.)]">
        <xsl:variable name="current" select="." as="element()"/>
        <xsl:variable name="first" select="node()[1]" as="node()?"/>
        <xsl:for-each-group select="node()" group-adjacent="dita-ot:is-block(.)">
          <xsl:choose>
            <xsl:when test="current-grouping-key()">
              <xsl:apply-templates select="current-group()" mode="flatten"/>
            </xsl:when>
            <xsl:when test="count(current-group()) eq 1 and current-group()/self::text() and not(normalize-space(current-group()))"/>
            <xsl:when test="parent::*[contains(@class, ' topic/li ')] and $first is current-group()[1]">
              <p gen="4" class="- topic/p ">
                <xsl:apply-templates select="current-group()" mode="flatten"/>
              </p>
            </xsl:when>
            <xsl:otherwise>
              <p gen="1" class="- topic/p ">
                <xsl:apply-templates select="$current/@* except $current/@id | current-group()" mode="flatten"/>
              </p>
            </xsl:otherwise>  
          </xsl:choose>
        </xsl:for-each-group>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="@* | node()" mode="flatten"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- wrapper elements -->
  <xsl:template match="*[dita-ot:is-container-block(.)]" mode="flatten" priority="10">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="flatten"/>
      <xsl:variable name="first" select="node()[1]" as="node()?"/>
      <xsl:for-each-group select="node()" group-adjacent="dita-ot:is-block(.)">
        <xsl:choose>
          <xsl:when test="current-grouping-key()">
            <xsl:apply-templates select="current-group()" mode="flatten"/>
          </xsl:when>
          <xsl:when test="count(current-group()) eq 1 and current-group()/self::text() and not(normalize-space(current-group()))"/>
          <xsl:when test="parent::*[contains(@class, ' topic/li ')] and $first is current-group()[1]">
            <p gen="2" class="- topic/p ">
              <xsl:apply-templates select="current-group()" mode="flatten"/>
            </p>
          </xsl:when>
          <xsl:otherwise>
            <p gen="3" class="- topic/p ">
              <xsl:apply-templates select="current-group()" mode="flatten"/>
            </p>
          </xsl:otherwise>  
        </xsl:choose>
      </xsl:for-each-group>
    </xsl:copy>
  </xsl:template>
    
</xsl:stylesheet>