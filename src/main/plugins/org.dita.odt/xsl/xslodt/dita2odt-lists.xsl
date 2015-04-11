<?xml version="1.0" encoding="UTF-8"?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses. -->
<!-- (c) Copyright IBM Corp. 2005, 2006 All Rights Reserved. -->
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
  exclude-result-prefixes="xs"
  version="2.0">

   <!-- Ordered and unordered list -->

   <xsl:template match="*[contains(@class,' topic/ul ')]">
     <xsl:call-template name="render_list">
       <xsl:with-param name="list_style" select="'list_style'"/>
     </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' topic/ol ')]">
     <xsl:call-template name="render_list">
       <xsl:with-param name="list_style" select="'ordered_list_style'"/>
     </xsl:call-template>     
   </xsl:template>
     
   <xsl:template match="*[contains(@class,' topic/sl ')]">
     <xsl:call-template name="render_list">
       <xsl:with-param name="list_style" select="'list_style'"/>
     </xsl:call-template>
   </xsl:template>
   
   <xsl:template match="*[contains(@class,' topic/li ')]">
     <text:list-item>
       <xsl:apply-templates/>
     </text:list-item>
   </xsl:template>
  
  <!-- Simple list -->
   
   <xsl:template match="*[contains(@class,' topic/sli ')]">
     <xsl:call-template name="block-sli"/>
   </xsl:template>
   
  <xsl:template name="block-sli">
    <text:list-item>
      <xsl:apply-templates/>
    </text:list-item>
  </xsl:template>
   
   <!-- XXX: Not used -->
   <xsl:template name="block-li">
     <!-- FIXME: variable as not used, should they? -->
     <xsl:variable name="depth" select="count(ancestor::*[contains(@class,' topic/li ')])"/>
     <xsl:variable name="li-num" select="420 + ($depth * 420)"/>
     <xsl:variable name="listnum" select="count(preceding::*[contains(@class,' topic/ol ') or contains(@class,' topic/ul ')]
                                                            [not(ancestor::*[contains(@class,' topic/li ')])]) + 1"/>
     <text:list-item>
       <xsl:apply-templates mode="create_list_item"/>
     </text:list-item>
   </xsl:template>
   
   <xsl:template match="* | text()" mode="create_list_item">
     <xsl:apply-templates select="."/>
   </xsl:template>

   <!-- Definition list -->
  
   <xsl:template match="*[contains(@class,' topic/dl ')]">
     <!-- render list -->
     <xsl:call-template name="render_list">
       <xsl:with-param name="list_style" select="'list_style'"/>
     </xsl:call-template>
   </xsl:template>
     
   <!-- definition list -->
   <!-- for dl tag -->
   <xsl:template name="block-lq">
     <xsl:choose>
       <!-- nested by p -->
       <xsl:when test="parent::*[contains(@class, ' topic/p ')]">
         <!-- break p tag -->
         <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
         <!-- start render dl -->
         <text:p>
           <xsl:apply-templates/>
         </text:p>
         <!-- start p tag again -->
         <xsl:text disable-output-escaping="yes">&lt;text:p&gt;</xsl:text>
       </xsl:when>
       <!-- nested by note -->
       <xsl:when test="parent::*[contains(@class, ' topic/note ')]">
         <!-- break p tag -->
         <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
         <!-- start render dl -->
         <text:p>
           <xsl:apply-templates/>
         </text:p>
         <!-- start p tag again -->
         <xsl:text disable-output-escaping="yes">&lt;text:p&gt;</xsl:text>
       </xsl:when>
       <!-- nested by lq -->
       <xsl:when test="parent::*[contains(@class, ' topic/lq ')]">
         <xsl:apply-templates/>
       </xsl:when>
       <!-- nested by itemgroup -->
       <xsl:when test="parent::*[contains(@class, ' topic/itemgroup ')]">
         <xsl:apply-templates/>
       </xsl:when>
       <xsl:otherwise>
         <text:p>
           <xsl:apply-templates/>
         </text:p>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:template>
     
   <!-- dlhead tag-->
   <xsl:template match="*[contains(@class, ' topic/dlhead ')]" name="topic.dlhead">
     <text:list-item>
       <xsl:apply-templates/>
     </text:list-item>
   </xsl:template>
   
   <!-- DL heading, term -->
   <xsl:template match="*[contains(@class,' topic/dthd ')]" name="topic.dthd">
     <text:p text:style-name="bold_paragraph">
       <text:span>
         <xsl:apply-templates select="." mode="start-add-odt-flags"/>
         <xsl:apply-templates/>
         <xsl:apply-templates select="." mode="end-add-odt-flags"/>
       </text:span>
     </text:p>
   </xsl:template>
     
   <!-- DL heading, description -->
   <xsl:template match="*[contains(@class,' topic/ddhd ')]" name="topic.ddhd">
     <text:p text:style-name="bold_paragraph">
       <text:span>
         <xsl:apply-templates select="." mode="start-add-odt-flags"/>
         <text:tab/>
         <xsl:apply-templates/>
         <xsl:apply-templates select="." mode="end-add-odt-flags"/>
       </text:span>
     </text:p>
   </xsl:template>
   
   <!-- dlentry tag-->
   <xsl:template match="*[contains(@class,' topic/dlentry ')]" name="topic.dlentry">
     <text:list-item>
       <xsl:apply-templates/>
     </text:list-item>
   </xsl:template>
   
   <!-- for dt tag -->
   <xsl:template match="*[contains(@class, ' topic/dt ')]">
     <text:p text:style-name="bold_paragraph">
       <text:span>
         <xsl:apply-templates select="." mode="start-add-odt-flags"/>
         <xsl:apply-templates/>
         <xsl:apply-templates select="." mode="end-add-odt-flags"/>	
       </text:span>
     </text:p>
   </xsl:template>
   
   <!-- for dd tag -->
   <xsl:template match="*[contains(@class, ' topic/dd ')]">
     <text:p>
       <text:span>
         <xsl:apply-templates select="." mode="start-add-odt-flags"/>
         <text:tab/>
           <xsl:apply-templates/>
         <xsl:apply-templates select="." mode="end-add-odt-flags"/>
       </text:span>
     </text:p>
   </xsl:template>
   
   <!-- parameter list -->
   
   <xsl:template match="parml"> <!-- not found -->
     <xsl:call-template name="block-lq"/>
   </xsl:template>
   
   <xsl:template match="plentry/synph">  <!-- plentry not found -->
     <xsl:call-template name="inline-em"/>
   </xsl:template>
   
   <xsl:template match="plentry/li">  <!-- plentry not found -->
     <xsl:call-template name="block-lq"/>
   </xsl:template>
   	
   <!-- block-list -->
   <xsl:template name="block-list"/>
   
   <xsl:template name="block-ol"/>
   
   <xsl:template name="gen-list-table"/>
   
   	
   <xsl:template match="*[contains(@class,' topic/ol ')]" mode="gen-list-table"/>
   	
   <xsl:template match="*[contains(@class,' topic/ul ')]" mode="gen-list-table"/>
	
	  
  <!-- FIXME: Replace with something that doens't use DOE -->
  <xsl:template name="render_list">
    <xsl:param name="list_style"/>

    <xsl:variable name="li_count_for_table" select="count(ancestor::*[contains(@class, ' topic/li ')]) - 
      count(ancestor::*[contains(@class, ' topic/entry ')][1]/ancestor::*[contains(@class, ' topic/li ')])"/>
    
    <xsl:variable name="li_count_for_simpletable" select="count(ancestor::*[contains(@class, ' topic/li ')]) - 
      count(ancestor::*[contains(@class, ' topic/stentry ')][1]/ancestor::*[contains(@class, ' topic/li ')])"/>
    
    <xsl:variable name="dlentry_count_for_table" select="count(ancestor::*[contains(@class, ' topic/dlentry ')]) - 
      count(ancestor::*[contains(@class, ' topic/entry ')][1]/ancestor::*[contains(@class, ' topic/dlentry ')])"/>
    
    <xsl:variable name="dlentry_count_for_simpletable" select="count(ancestor::*[contains(@class, ' topic/dlentry ')]) - 
      count(ancestor::*[contains(@class, ' topic/stentry ')][1]/ancestor::*[contains(@class, ' topic/dlentry ')])"/>
    
    <xsl:variable name="dlentry_count_for_list" select="count(ancestor::*[contains(@class, ' topic/dlentry ')]) - 
      count(ancestor::*[contains(@class, ' topic/li ')][1]/ancestor::*[contains(@class, ' topic/dlentry ')])"/>
    
    <xsl:message>$li_count_for_table <xsl:value-of select="$li_count_for_table"/> $dlentry_count_for_table <xsl:value-of select="$dlentry_count_for_table"/></xsl:message>
    
    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/body ')] or
                      parent::*[contains(@class, ' topic/li ')] or
                      parent::*[contains(@class, ' topic/sli ')] or
                      parent::*[contains(@class, ' topic/entry ')] or
                      parent::*[contains(@class, ' topic/stentry ')]">
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
      </xsl:when>
      <!-- parent tag is fn -->
      <xsl:when test="parent::*[contains(@class, ' topic/fn ')]">
        <!-- break span tag(for flagging)-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="1"/>
          <xsl:with-param name="order" select="0"/>
        </xsl:call-template>
        <!-- break p tag -->
        <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <!-- start p tag again -->
        <xsl:text disable-output-escaping="yes">&lt;text:p text:style-name="footnote"&gt;</xsl:text>
        <!--  start render span tags again-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="1"/>
          <xsl:with-param name="order" select="1"/>
        </xsl:call-template>
      </xsl:when>
      <!-- nearest ancestor tag is table -->
      <xsl:when test="ancestor::*[contains(@class, ' topic/entry ')] and $li_count_for_table = 0 and $dlentry_count_for_table = 0">
        <xsl:variable name="span_depth">
          <xsl:call-template name="calculate_span_depth_for_tag">
            <xsl:with-param name="tag_class" select="' topic/entry '"/>
          </xsl:call-template>
        </xsl:variable>
        <!-- break span tags -->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="0"/>
        </xsl:call-template>
        <!-- break first p tag -->
        <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <!-- start first p tag again -->
        <xsl:text disable-output-escaping="yes">&lt;text:p&gt;</xsl:text>
        <!--  start render span tags again-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="1"/>
        </xsl:call-template>
      </xsl:when>
      <!-- nearest ancestor tag is simpletable -->
      <xsl:when test="ancestor::*[contains(@class, ' topic/stentry ')] and $li_count_for_simpletable = 0 
                      and $dlentry_count_for_simpletable = 0">
        <xsl:variable name="span_depth">
          <xsl:call-template name="calculate_span_depth_for_tag">
            <xsl:with-param name="tag_class" select="' topic/stentry '"/>
          </xsl:call-template>
        </xsl:variable>
        <!-- break span tags -->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="0"/>
        </xsl:call-template>
        <!-- break first p tag -->
        <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <!-- start first p tag again -->
        <xsl:text disable-output-escaping="yes">&lt;text:p&gt;</xsl:text>
        <!--  start render span tags again-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="1"/>
        </xsl:call-template>
      </xsl:when>
      
      <!-- nearest ancestor tag is li -->
      <xsl:when test="ancestor::*[contains(@class, ' topic/li ')] and $dlentry_count_for_list = 0">
        <xsl:variable name="span_depth">
          <xsl:call-template name="calculate_span_depth_for_tag">
            <xsl:with-param name="tag_class" select="' topic/li '"/>
          </xsl:call-template>
        </xsl:variable>
        <!-- break span tags -->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="0"/>
        </xsl:call-template>
        <!-- break first p tag -->
        <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <!-- start first p tag again -->
        <xsl:text disable-output-escaping="yes">&lt;text:p&gt;</xsl:text>
        <!--  start render span tags again-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="1"/>
        </xsl:call-template>
      </xsl:when>
      
      <!-- nearest ancestor tag is dlentry -->
      <xsl:when test="ancestor::*[contains(@class, ' topic/dlentry ')]">
        <xsl:variable name="span_depth">
          <xsl:call-template name="calculate_span_depth_for_tag">
            <xsl:with-param name="tag_class" select="' topic/dlentry '"/>
          </xsl:call-template>
        </xsl:variable>
        <!-- break span tags -->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="0"/>
        </xsl:call-template>
        <!-- break first p tag -->
        <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <!-- start first p tag again -->
        <xsl:text disable-output-escaping="yes">&lt;text:p&gt;</xsl:text>
        <!--  start render span tags again-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="1"/>
        </xsl:call-template>
      </xsl:when>
      <!-- nearest ancestor tag is fn -->
      <xsl:when test="ancestor::*[contains(@class, ' topic/fn ')]">
        <xsl:variable name="span_depth">
          <xsl:call-template name="calculate_span_depth_for_tag"/>
        </xsl:variable>
        <!-- break span tags -->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="0"/>
        </xsl:call-template>
        <!-- break first p tag -->
        <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <!-- start first p tag again -->
        <xsl:text disable-output-escaping="yes">&lt;text:p text:style-name="footnote"&gt;</xsl:text>
        <!--  start render span tags again-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="1"/>
        </xsl:call-template>
      </xsl:when>
      <!-- nested by other tags. -->
      <xsl:otherwise>
        <xsl:variable name="span_depth" as="xs:integer">
          <xsl:call-template name="calculate_span_depth"/>
        </xsl:variable>
        <!-- break span tags -->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="0"/>
        </xsl:call-template>
        <!-- break first p tag -->
        <xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <text:list text:style-name="{$list_style}">
          <xsl:apply-templates/>
        </text:list>
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_list'"/>
        </xsl:apply-templates>
        <!-- start first p tag again -->
        <xsl:text disable-output-escaping="yes">&lt;text:p&gt;</xsl:text>
        <!--  start render span tags again-->
        <xsl:call-template name="break_span_tags">
          <xsl:with-param name="depth" select="$span_depth"/>
          <xsl:with-param name="order" select="1"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
	
</xsl:stylesheet>