<?xml version="1.0" encoding="UTF-8" ?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses.-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                version="2.0">
     
  <xsl:template match="*[contains(@class,' xml-d/xmlelement ')]">    
    <text:span text:style-name="Courier_New">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:text>&lt;</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>&gt;</xsl:text>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>

  <xsl:template match="*[contains(@class,' xml-d/xmlatt ')]">    
    <text:span text:style-name="Courier_New">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:text>@</xsl:text>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' xml-d/textentity ')]">    
    <text:span text:style-name="Courier_New">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:text>&amp;</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>;</xsl:text>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' xml-d/parameterentity ')]">
    <text:span text:style-name="Courier_New">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:text>%</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>;</xsl:text>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>

  <xsl:template match="*[contains(@class,' xml-d/numcharref ')]">
    <text:span text:style-name="Courier_New">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:text>&amp;#</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>;</xsl:text>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>

  <xsl:template match="*[contains(@class,' xml-d/xmlnsname ')]">
    <text:span text:style-name="Courier_New">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>

  <xsl:template match="*[contains(@class,' xml-d/xmlnsname ')] |
                       *[contains(@class,' xml-d/xmlpi ')]">
    <text:span text:style-name="Courier_New">
      <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
      <xsl:apply-templates/>
      <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    </text:span>
  </xsl:template>
  
</xsl:stylesheet>