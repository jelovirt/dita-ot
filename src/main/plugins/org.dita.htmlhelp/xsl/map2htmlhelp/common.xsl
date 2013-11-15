<?xml version="1.0"?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for 
     applicable licenses.-->
<!-- (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl">

<!-- Template to get the relative path to a map -->
<xsl:template name="getRelativePath">
  <xsl:param name="remainingPath" select="@file"/>
  <xsl:choose>
    <xsl:when test="contains($remainingPath,'/')">
      <xsl:value-of select="substring-before($remainingPath,'/')"/><xsl:text>/</xsl:text>
      <xsl:call-template name="getRelativePath">
        <xsl:with-param name="remainingPath" select="substring-after($remainingPath,'/')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="contains($remainingPath,'\')">
      <xsl:value-of select="substring-before($remainingPath,'\')"/><xsl:text>/</xsl:text>
      <xsl:call-template name="getRelativePath">
        <xsl:with-param name="remainingPath" select="substring-after($remainingPath,'\')"/>
      </xsl:call-template>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!-- Remove all extra relpaths (as in './multiple/directories/../../other/') -->
<xsl:template name="removeAllExtraRelpath">
  <xsl:param name="remainingPath"><xsl:value-of select="@href"/></xsl:param>
  <xsl:variable name="firstRoundRemainingPath">
    <xsl:call-template name="removeExtraRelpath">
      <xsl:with-param name="remainingPath">
        <xsl:value-of select="$remainingPath"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="secondRoundRemainingPath">
    <xsl:call-template name="removeExtraRelpath">
      <xsl:with-param name="remainingPath">
        <xsl:value-of select="$firstRoundRemainingPath"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="contains($secondRoundRemainingPath, '../') and not($firstRoundRemainingPath=$secondRoundRemainingPath)">
      <xsl:call-template name="removeAllExtraRelpath">
        <xsl:with-param name="remainingPath" select="$secondRoundRemainingPath"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$secondRoundRemainingPath"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Remove extra relpaths (as in abc/../def) -->
<xsl:template name="removeExtraRelpath">
  <xsl:param name="remainingPath"><xsl:value-of select="@href"/></xsl:param>
  <xsl:choose>
    <xsl:when test="contains($remainingPath,'\')">
      <xsl:call-template name="removeExtraRelpath">
        <xsl:with-param name="remainingPath"><xsl:value-of 
          select="substring-before($remainingPath,'\')"/>/<xsl:value-of 
          select="substring-after($remainingPath,'\')"/></xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="starts-with($remainingPath,'./')">
      <xsl:call-template name="removeExtraRelpath">
        <xsl:with-param name="remainingPath" select="substring-after($remainingPath,'./')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="not(contains($remainingPath,'../'))"><xsl:value-of select="$remainingPath"/></xsl:when>
    <xsl:when test="not(starts-with($remainingPath,'../')) and
                    starts-with(substring-after($remainingPath,'/'),'../')">
      <xsl:call-template name="removeExtraRelpath">
        <xsl:with-param name="remainingPath" select="substring-after($remainingPath,'../')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="contains($remainingPath,'/')">
      <xsl:value-of select="substring-before($remainingPath,'/')"/>/<xsl:text/>
      <xsl:call-template name="removeExtraRelpath">
        <xsl:with-param name="remainingPath" select="substring-after($remainingPath,'/')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$remainingPath"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>
  
</xsl:stylesheet>
