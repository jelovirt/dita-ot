<?xml version="1.0" encoding="UTF-8" ?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses. -->
<!-- (c) Copyright IBM Corp. 2005 All Rights Reserved. -->

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
     xmlns:styleUtils="org.dita.dost.util.StyleUtils"
     exclude-result-prefixes="styleUtils xs"
     version="2.0">

  <xsl:output method="xml"/>
  <xsl:output indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="*[contains(@class,' hi-d/b ')]">
    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/li ')] or parent::*[contains(@class, ' topic/sli ')]">
        <text:p>
          <xsl:apply-templates/>
        </text:p>
      </xsl:when>
      <!-- nested by entry -->
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]">
        <!-- alignment styles -->
        <xsl:if test="parent::*[contains(@class, ' topic/entry ')]/@align">
          <xsl:call-template name="set_align_value"/>
        </xsl:if>
        <xsl:apply-templates/>
      </xsl:when>
      <!-- nested by stentry -->
      <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]">
        <text:p>
          <xsl:apply-templates/>
        </text:p>
      </xsl:when>
      <!-- nested by other tags -->
      <xsl:otherwise>
        <!-- 
               <xsl:element name="text:span">
                    <xsl:attribute name="text:style-name">bold</xsl:attribute>
               -->
        <xsl:apply-templates/>
        <!-- 
               </xsl:element>
               -->
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="*[contains(@class,' hi-d/i ')]">

    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/li ')] or parent::*[contains(@class, ' topic/sli ')]">
        <text:p>
          <xsl:apply-templates/>
        </text:p>
      </xsl:when>
      <!-- nested by entry -->
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]">
        <!-- create p tag -->
        <text:p>
          <!-- alignment styles -->
          <xsl:if test="parent::*[contains(@class, ' topic/entry ')]/@align">
            <xsl:call-template name="set_align_value"/>
          </xsl:if>
          <!-- cell belongs to thead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/entry ')]
                              /parent::*[contains(@class, ' topic/row ')]/parent::*[contains(@class, ' topic/thead ')]">
              <text:span text:style-name="bold">

                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by stentry -->
      <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]">
        <text:p>
          <!-- cell belongs to sthead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]/
                              parent::*[contains(@class, ' topic/sthead ')]">
              <text:span text:style-name="bold">

                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by other tags -->
      <xsl:otherwise>
        <!-- 
               <xsl:element name="text:span">
                    <xsl:attribute name="text:style-name">italic</xsl:attribute>
                    <xsl:apply-templates/>
               </xsl:element>
               -->
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*[contains(@class,' hi-d/u ')]">
    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/li ')] or parent::*[contains(@class, ' topic/sli ')]">
        <text:p>
          <xsl:apply-templates/>
        </text:p>
      </xsl:when>
      <!-- nested by entry -->
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]">
        <!-- create p tag -->
        <text:p>
          <!-- alignment styles -->
          <xsl:if test="parent::*[contains(@class, ' topic/entry ')]/@align">
            <xsl:call-template name="set_align_value"/>
          </xsl:if>
          <!-- cell belongs to thead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/entry ')]
                              /parent::*[contains(@class, ' topic/row ')]/parent::*[contains(@class, ' topic/thead ')]">
              <text:span text:style-name="bold">
                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by stentry -->
      <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]">
        <text:p>
          <!-- cell belongs to sthead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]/
                              parent::*[contains(@class, ' topic/sthead ')]">
              <text:span text:style-name="bold">
                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by other tags -->
      <xsl:otherwise>
        <!-- 
               <xsl:element name="text:span">
                    <xsl:attribute name="text:style-name">underline</xsl:attribute>
                    <xsl:apply-templates/>
               </xsl:element>
               -->
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="*[contains(@class,' hi-d/tt ')]">
    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/li ')] or parent::*[contains(@class, ' topic/sli ')]">
        <text:p>
          <text:span text:style-name="Courier">
            <xsl:apply-templates/>
          </text:span>
        </text:p>
      </xsl:when>
      <!-- nested by entry -->
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]">
        <!-- create p tag -->
        <text:p>
          <!-- alignment styles -->
          <xsl:if test="parent::*[contains(@class, ' topic/entry ')]/@align">
            <xsl:call-template name="set_align_value"/>
          </xsl:if>
          <text:span text:style-name="Courier">
            <xsl:apply-templates/>
          </text:span>
        </text:p>
      </xsl:when>
      <!-- nested by stentry -->
      <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]">
        <text:p>
          <text:span text:style-name="Courier">
            <xsl:apply-templates/>
          </text:span>
        </text:p>
      </xsl:when>
      <!-- nested by other tags -->
      <xsl:otherwise>
        <text:span text:style-name="Courier">
          <xsl:apply-templates/>
        </text:span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*[contains(@class,' hi-d/sup ')]">
    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/li ')] or parent::*[contains(@class, ' topic/sli ')]">
        <text:p>
          <xsl:apply-templates/>
        </text:p>
      </xsl:when>
      <!-- nested by entry -->
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]">
        <!-- create p tag -->
        <text:p>
          <!-- alignment styles -->
          <xsl:if test="parent::*[contains(@class, ' topic/entry ')]/@align">
            <xsl:call-template name="set_align_value"/>
          </xsl:if>
          <!-- cell belongs to thead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/entry ')]                               /parent::*[contains(@class, ' topic/row ')]/parent::*[contains(@class, ' topic/thead ')]">
              <text:span text:style-name="bold">
                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by stentry -->
      <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]">
        <text:p>
          <!-- cell belongs to sthead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]/                               parent::*[contains(@class, ' topic/sthead ')]">
              <text:span text:style-name="bold">
                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by other tags -->
      <xsl:otherwise>
        <!-- 
               <xsl:element name="text:span">
                    <xsl:attribute name="text:style-name">sup</xsl:attribute>
                    <xsl:apply-templates/>
               </xsl:element>
               -->
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*[contains(@class,' hi-d/sub ')]">
    <xsl:choose>
      <xsl:when test="parent::*[contains(@class, ' topic/li ')] or parent::*[contains(@class, ' topic/sli ')]">
        <text:p>
          <xsl:apply-templates/>
        </text:p>
      </xsl:when>
      <!-- nested by entry -->
      <xsl:when test="parent::*[contains(@class, ' topic/entry ')]">
        <!-- create p tag -->
        <text:p>
          <!-- alignment styles -->
          <xsl:if test="parent::*[contains(@class, ' topic/entry ')]/@align">
            <xsl:call-template name="set_align_value"/>
          </xsl:if>
          <!-- cell belongs to thead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/entry ')]                               /parent::*[contains(@class, ' topic/row ')]/parent::*[contains(@class, ' topic/thead ')]">
              <text:span text:style-name="bold">
                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by stentry -->
      <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]">
        <text:p>
          <!-- cell belongs to sthead -->
          <xsl:choose>
            <xsl:when test="parent::*[contains(@class, ' topic/stentry ')]/                               parent::*[contains(@class, ' topic/sthead ')]">
              <text:span text:style-name="bold">
                <xsl:apply-templates/>
              </text:span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates/>
            </xsl:otherwise>
          </xsl:choose>
        </text:p>
      </xsl:when>
      <!-- nested by other tags -->
      <xsl:otherwise>
        <!-- 
               <xsl:element name="text:span">
                    <xsl:attribute name="text:style-name">sub</xsl:attribute>
                    <xsl:apply-templates/>
               </xsl:element>
               -->
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text()|*[contains(@class, ' topic/state ')]" mode="create_hi_style">
    <!-- generating style name based on the styles used on the text. -->
    <xsl:variable name="styles" as="xs:string*">
      <xsl:call-template name="get_style_name"/>
    </xsl:variable>
    <xsl:variable name="style_name" select="string-join($styles, '')" as="xs:string?"/>
    <xsl:message>style: "<xsl:value-of select="$style_name"/>"</xsl:message>
    <xsl:variable name="hasStyleName" select="styleUtils:insertHiStyleName($style_name)"/>
    <xsl:if test="exists($styles) and $hasStyleName = 'false'">
      <!-- common hi style -->
      <style:style style:name="{$style_name}" style:family="text">
        <style:text-properties>
          <!-- bold-->
          <xsl:if test="$styles = 'bold'">
            <xsl:attribute name="fo:font-weight">bold</xsl:attribute>
            <xsl:attribute name="style:font-weight-asian">bold</xsl:attribute>
            <xsl:attribute name="style:font-weight-complex">bold</xsl:attribute>
          </xsl:if>
          <!-- italic -->
          <xsl:if test="$styles = 'italic'">
            <xsl:attribute name="fo:font-style">italic</xsl:attribute>
            <xsl:attribute name="style:font-style-asian">italic</xsl:attribute>
            <xsl:attribute name="style:font-style-complex">italic</xsl:attribute>
          </xsl:if>
          <!-- underline -->
          <xsl:if test="$styles = 'underline'">
            <xsl:attribute name="style:text-underline-style">solid</xsl:attribute>
            <xsl:attribute name="style:text-underline-type">single</xsl:attribute>
            <xsl:attribute name="style:text-underline-width">auto</xsl:attribute>
            <xsl:attribute name="style:text-underline-color">font-color</xsl:attribute>
          </xsl:if>
          <!-- sub -->
          <xsl:choose>
            <xsl:when test="$styles = 'sub1'">
              <xsl:attribute name="style:text-position">-10% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub2'">
              <xsl:attribute name="style:text-position">-20% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub3'">
              <xsl:attribute name="style:text-position">-30% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub4'">
              <xsl:attribute name="style:text-position">-40% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub5'">
              <xsl:attribute name="style:text-position">-50% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub6'">
              <xsl:attribute name="style:text-position">-60% 58%</xsl:attribute>
            </xsl:when>
          </xsl:choose>
          <!-- sup -->
          <xsl:choose>
            <xsl:when test="$styles = 'sup1'">
              <xsl:attribute name="style:text-position">40% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup2'">
              <xsl:attribute name="style:text-position">50% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup3'">
              <xsl:attribute name="style:text-position">60% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup4'">
              <xsl:attribute name="style:text-position">70% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup5'">
              <xsl:attribute name="style:text-position">80% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup6'">
              <xsl:attribute name="style:text-position">90% 58%</xsl:attribute>
            </xsl:when>
          </xsl:choose>
          <!-- line-through -->
          <xsl:if test="$styles = 'line-through'">
            <xsl:attribute name="style:text-line-through-style">solid</xsl:attribute>
            <xsl:attribute name="style:text-line-through-type">single</xsl:attribute>
            <xsl:attribute name="style:text-line-through-width">auto</xsl:attribute>
            <xsl:attribute name="style:text-line-through-color">font-color</xsl:attribute>
          </xsl:if>
          <!-- overline -->
          <xsl:if test="$styles = 'overline'">
            <xsl:attribute name="style:text-overline-style">solid</xsl:attribute>
            <xsl:attribute name="style:text-overline-type">single</xsl:attribute>
            <xsl:attribute name="style:text-overline-width">auto</xsl:attribute>
            <xsl:attribute name="style:text-overline-color">font-color</xsl:attribute>
          </xsl:if>
          <!-- codeblock/screen -->
          <xsl:if test="$styles = 'code'">
            <xsl:attribute name="fo:background-color">#d9d9d9</xsl:attribute>
          </xsl:if>
        </style:text-properties>
      </style:style>
      <!-- boolean hi style -->
      <style:style style:name="{concat('boolean_', $style_name)}" style:family="text" style:parent-style-name="boolean_style">
        <style:text-properties>
          <!-- bold-->
          <xsl:if test="$styles = 'bold'">
            <xsl:attribute name="fo:font-weight">bold</xsl:attribute>
            <xsl:attribute name="style:font-weight-asian">bold</xsl:attribute>
            <xsl:attribute name="style:font-weight-complex">bold</xsl:attribute>
          </xsl:if>
          <!-- italic -->
          <xsl:if test="$styles = 'italic'">
            <xsl:attribute name="fo:font-style">italic</xsl:attribute>
            <xsl:attribute name="style:font-style-asian">italic</xsl:attribute>
            <xsl:attribute name="style:font-style-complex">italic</xsl:attribute>
          </xsl:if>
          <!-- underline -->
          <xsl:if test="$styles = 'underline'">
            <xsl:attribute name="style:text-underline-style">solid</xsl:attribute>
            <xsl:attribute name="style:text-underline-type">single</xsl:attribute>
            <xsl:attribute name="style:text-underline-width">auto</xsl:attribute>
            <xsl:attribute name="style:text-underline-color">font-color</xsl:attribute>
          </xsl:if>
          <!-- sub -->
          <xsl:choose>
            <xsl:when test="$styles = 'sub1'">
              <xsl:attribute name="style:text-position">-10% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub2'">
              <xsl:attribute name="style:text-position">-20% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub3'">
              <xsl:attribute name="style:text-position">-30% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub4'">
              <xsl:attribute name="style:text-position">-40% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sub5'">
              <xsl:attribute name="style:text-position">-50% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when  test="$styles = 'sub6'">
              <xsl:attribute name="style:text-position">-60% 58%</xsl:attribute>
            </xsl:when>
          </xsl:choose>
          <!-- sup -->
          <xsl:choose>
            <xsl:when test="$styles = 'sup1'">
              <xsl:attribute name="style:text-position">40% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup2'">
              <xsl:attribute name="style:text-position">50% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup3'">
              <xsl:attribute name="style:text-position">60% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup4'">
              <xsl:attribute name="style:text-position">70% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup5'">
              <xsl:attribute name="style:text-position">80% 58%</xsl:attribute>
            </xsl:when>
            <xsl:when test="$styles = 'sup6'">
              <xsl:attribute name="style:text-position">90% 58%</xsl:attribute>
            </xsl:when>
          </xsl:choose>
          <!-- codeblock/screen -->
          <xsl:if test="$styles = 'code'">
            <xsl:attribute name="fo:background-color">#d9d9d9</xsl:attribute>
          </xsl:if>
        </style:text-properties>
      </style:style>
    </xsl:if>
  </xsl:template>

  <xsl:template name="get_style_name" as="xs:string*">
    <xsl:variable name="styles" as="xs:string*">
      <xsl:apply-templates select="parent::*" mode="get_style_name"/>
    </xsl:variable>
    <xsl:for-each select="distinct-values($styles)">
      <xsl:sort/>
      <xsl:value-of select="."/>
    </xsl:for-each>
    <!--
    <xsl:if test="ancestor::*[contains(@class, ' hi-d/i ')] | ancestor::*[contains(@class, ' topic/term ')] |            ancestor::*[contains(@class, ' topic/cite ')]">
      <xsl:value-of select="'italic'"/>
    </xsl:if>
    <xsl:if test="ancestor::*[contains(@class, ' hi-d/u ')]">
      <xsl:value-of select="'underline'"/>
    </xsl:if>
    <xsl:if test="ancestor::*[contains(@class, ' hi-d/b ')]">
      <xsl:value-of select="'bold'"/>
    </xsl:if>
    <xsl:if test="ancestor::*[contains(@class, ' hi-d/sub ')]">
      <xsl:value-of select="'sub'"/>
      <xsl:variable name="depth" select="count(ancestor::*[contains(@class, ' hi-d/sub ')])"/>
      <xsl:choose>
        <xsl:when test="$depth &gt; 5">
          <xsl:value-of select="6"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$depth"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="ancestor::*[contains(@class, ' hi-d/sup ')]">
      <xsl:value-of select="'sup'"/>
      <xsl:variable name="depth" select="count(ancestor::*[contains(@class, ' hi-d/sup ')])"/>
      <xsl:choose>
        <xsl:when test="$depth &gt; 5">
          <xsl:value-of select="6"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$depth"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="ancestor::*[contains(@class, ' ui-d/screen ')] | ancestor::*[contains(@class, ' pr-d/codeblock ')]">
      <xsl:value-of select="'code'"/>
    </xsl:if>
    <xsl:if test="ancestor::*[contains(@class, ' hi-d/line-through ')]">
      <xsl:text>line-through</xsl:text>
    </xsl:if>
    <xsl:if test="ancestor::*[contains(@class, ' hi-d/overline ')]">
      <xsl:text>overline</xsl:text>
    </xsl:if>
    -->
  </xsl:template>
  
  <xsl:template match="*" mode="get_style_name" as="xs:string*" priority="-10">
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' hi-d/i ')] |
                       *[contains(@class, ' topic/term ')] |
                       *[contains(@class, ' topic/cite ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:text>italic</xsl:text>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' hi-d/u ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:text>underline</xsl:text>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' hi-d/b ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:text>bold</xsl:text>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' ui-d/screen ')] | *[contains(@class, ' pr-d/codeblock ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:text>code</xsl:text>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' hi-d/line-through ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:text>line-through</xsl:text>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' hi-d/overline ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:text>overline</xsl:text>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class, ' hi-d/sub ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:variable name="depth" select="count(ancestor::*[contains(@class, ' hi-d/sub ')])"/>
    <xsl:value-of select="concat('sub', if ($depth gt 6) then 6 else $depth)"/>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>

  <xsl:template match="*[contains(@class, ' hi-d/sup ')]"
                mode="get_style_name" as="xs:string*">
    <xsl:variable name="depth" select="count(ancestor::*[contains(@class, ' hi-d/sup ')])"/>
    <xsl:value-of select="concat('sup', if ($depth gt 6) then 6 else $depth)"/>
    <xsl:apply-templates select="parent::*" mode="get_style_name"/>
  </xsl:template>

</xsl:stylesheet>
