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
  xmlns:dita-ot="http://dita-ot.sourceforge.net/ns/201007/dita-ot"
  version="2.0" 
  exclude-result-prefixes="xs dita-ot">

  <xsl:function name="dita-ot:to-inch" as="xs:double">
    <xsl:param name="length" as="xs:string?"/>
    <xsl:choose>
      <xsl:when test="contains($length, 'in')">
        <xsl:sequence select="number(substring-before($length, 'in'))"/>
      </xsl:when>
      <xsl:when test="contains($length, 'cm')">
        <xsl:sequence select="number(substring-before($length, 'cm')) div 2.54"/>
      </xsl:when>
      <xsl:when test="contains($length, 'mm')">
        <xsl:sequence select="number(substring-before($length, 'mm')) div 25.4"/>
      </xsl:when>
      <xsl:when test="contains($length, 'pt')">
        <xsl:sequence select="number(substring-before($length, 'pt')) div 72"/>
      </xsl:when>
      <xsl:when test="contains($length, 'pc')">
        <xsl:sequence select="number(substring-before($length, 'pc')) div 6"/>
      </xsl:when>
      <xsl:when test="contains($length, 'px')">
        <xsl:sequence select="number(substring-before($length, 'px')) div 96"/>
      </xsl:when>
      <xsl:when test="contains($length, 'em')">
        <xsl:sequence select="number(substring-before($length, 'em')) div 6"/>
      </xsl:when>
      <!-- default is px -->
      <xsl:otherwise>
        <xsl:sequence select="number($length) div 96"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
  
  <xsl:template match="*[contains(@class,' topic/image ')]">
  <xsl:if test="@href and not(@href='')">
    <!-- image meta data -->
      <xsl:variable name="scale" as="xs:double">
        <xsl:choose>
          <xsl:when test="@scale">
            <xsl:sequence select="@scale div 100"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:sequence select="1"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="height" as="xs:double?">
        <xsl:choose>
          <xsl:when test="@height">
            <xsl:value-of select="dita-ot:to-inch(@height) * $scale"/>
          </xsl:when>
          <xsl:when test="@dita-ot:image-height">
            <xsl:value-of select="dita-ot:to-inch(@dita-ot:image-height) * $scale"/>
          </xsl:when>
          <xsl:otherwise/>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="width" as="xs:double?">
        <xsl:choose>
          <xsl:when test="@width">
            <xsl:value-of select="dita-ot:to-inch(@width) * $scale"/>
          </xsl:when>
          <xsl:when test="@dita-ot:image-width">
            <xsl:value-of select="dita-ot:to-inch(@dita-ot:image-width) * $scale"/>
          </xsl:when>
          <xsl:otherwise/>
        </xsl:choose>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="@placement = 'break'">
          <xsl:call-template name="topic.p">
            <xsl:with-param name="contents">
              <xsl:call-template name="draw_image">
                <xsl:with-param name="height" select="$height"/>
                <xsl:with-param name="width" select="$width"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="." mode="start-add-odt-flags"/>
          <xsl:call-template name="draw_image">
            <xsl:with-param name="height" select="$height"/>
            <xsl:with-param name="width" select="$width"/>
          </xsl:call-template>
          <xsl:apply-templates select="." mode="end-add-odt-flags"/>    
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
  
  <xsl:attribute-set name="image">
    <xsl:attribute name="xlink:type">simple</xsl:attribute>
    <xsl:attribute name="xlink:show">embed</xsl:attribute>
    <xsl:attribute name="xlink:actuate">onLoad</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:template name="draw_image">
    <xsl:param name="height" as="xs:double?"/>
    <xsl:param name="width" as="xs:double?"/>
    <xsl:choose>
      <xsl:when test="not(contains(@href,'://')) and ($height gt 0) and ($width gt 0)">
        <draw:frame draw:style-name="fr{generate-id(.)}" text:anchor-type="{if (@placement = 'break') then 'paragraph' else 'as-char'}">
          <xsl:attribute name="svg:height" select="concat($height, 'in')"/>
          <xsl:attribute name="svg:width" select="concat(min(($width, 6)), 'in')"/>
          <draw:image xlink:href="Pictures/{@href}" xsl:use-attribute-sets="image"/>
        </draw:frame>
      </xsl:when>
      <xsl:otherwise>
        <text:a xlink:href="{@href}" xlink:type="simple">
          <xsl:call-template name="gen-img-txt"/>
        </text:a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
 
  <xsl:template name="gen-img-txt">
    <xsl:param name="alttext"/>
    <xsl:choose>
      <xsl:when test="$alttext != ''">
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="$alttext"/>
        </xsl:call-template>
      </xsl:when>
     <xsl:when test="*[contains(@class,' topic/alt ')]">
       <xsl:value-of select="*[contains(@class,' topic/alt ')]"/>
     </xsl:when>
     <xsl:when test="startflag/alt-text">
       <xsl:value-of select="startflag/alt-text"/>
     </xsl:when>
     <xsl:when test="@alt and not(@alt='')">
       <xsl:value-of select="@alt"/>
     </xsl:when>
     <xsl:when test="text() or *">
       <xsl:apply-templates/>
     </xsl:when>
     <xsl:when test="@href">
       <xsl:value-of select="@href"/>
     </xsl:when>
     <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="create_graphic_styles">
    <xsl:for-each select="//*[contains(@class, ' topic/image ')]">
      <style:style style:name="fr{generate-id(.)}" style:family="graphic" style:parent-style-name="Graphics">
        <style:graphic-properties style:vertical-pos="top" style:vertical-rel="baseline"
          style:horizontal-pos="from-left" style:horizontal-rel="paragraph"
          style:shadow="none" draw:shadow-opacity="100%"
          style:mirror="none" fo:clip="rect(0mm, 0mm, 0mm, 0mm)"
          draw:luminance="0%" draw:contrast="0%" draw:red="0%" draw:green="0%" draw:blue="0%" draw:gamma="100%"
          draw:color-inversion="false" draw:image-opacity="100%" draw:color-mode="standard"/>
      </style:style>  
    </xsl:for-each>
  </xsl:template>
    

</xsl:stylesheet>