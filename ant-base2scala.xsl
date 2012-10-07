<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:x="x"
                exclude-result-prefixes="xs x"
                version="2.0">

  <xsl:template match="copy">
    <xsl:for-each select="fileset">
      <xsl:choose>
        <xsl:when test="@includesfile">copy_list</xsl:when>
        <xsl:otherwise>copy</xsl:otherwise>
      </xsl:choose>
      <xsl:text>(</xsl:text>
      <xsl:value-of select="x:value(@dir)"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="x:value(../@todir)"/>
      <xsl:text>, </xsl:text>
      <xsl:value-of select="x:value(@includes | @includesfile)"/>
      <xsl:text>)&#xA;</xsl:text>
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="echo">
    <xsl:text>logger.log</xsl:text>
    <xsl:choose>
      <xsl:when test="@level = 'error'">Error</xsl:when>
      <xsl:when test="@level = 'warning'">Warn</xsl:when>
      <xsl:when test="@level = 'info' or empty(@level)">Info</xsl:when>
      <xsl:when test="@level = ('verbose', 'debug')">Debug</xsl:when>
    </xsl:choose>
    <xsl:text>(</xsl:text>
    <xsl:value-of select="x:value(.)"/>
    <xsl:text>)&#xa;</xsl:text>
  </xsl:template>
  
  <xsl:template match="tstamp">
    <xsl:for-each select="format">
      <xsl:text>Properties(</xsl:text>
      <xsl:value-of select="x:value(@property)"/>
      <xsl:text>) = </xsl:text>
      <xsl:text>"20120130"</xsl:text>
      <xsl:text>&#xa;</xsl:text>
    </xsl:for-each>
  </xsl:template>
  
  
  <xsl:template match="mkdir">
    <xsl:text>if (!</xsl:text>
    <xsl:value-of select="x:file(@dir)"/>
    <xsl:text>.exists())</xsl:text>
    <xsl:call-template name="x:start-block"></xsl:call-template>
    <xsl:text></xsl:text>
    <xsl:value-of select="x:file(@dir)"/>
    <xsl:text>.mkdirs()</xsl:text>
    <xsl:call-template name="x:end-block"/>
    <!--
    <xsl:call-template name="x:if">
      <xsl:with-param name="test">
        <xsl:text>!os.path.exists(</xsl:text>
        <xsl:value-of select="x:value(@dir)"/>
        <xsl:text>)</xsl:text>
      </xsl:with-param>
      <xsl:with-param name="body">
        <xsl:text>os.makedirs(</xsl:text>
        <xsl:value-of select="x:value(@dir)"/>
        <xsl:text>)</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
    -->
  </xsl:template>
  
  <!--
  <xsl:template name="x:if">
    <xsl:param name="indent" tunnel="yes"/>
    <xsl:param name="test"/>
    <xsl:param name="body"/>
    
    <!- -xsl:value-of select="$indent"/- ->
    <xsl:text>if (</xsl:text>
    <xsl:copy-of select="$test"/>
    <xsl:text>) {&#xa;</xsl:text>
    <xsl:for-each select="tokenize($body, '&#xA;')">
      <!- -xsl:value-of select="$indent"/- ->
      <xsl:text></xsl:text>
      <xsl:value-of select="."/>
      <xsl:text>&#xa;</xsl:text>
    </xsl:for-each>
    <!- -xsl:value-of select="$indent"/- ->
    <xsl:text>}&#xa;</xsl:text>
  </xsl:template>
  -->
  
  <xsl:template match="property">
    <xsl:text>Properties("</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>") = </xsl:text>
    <xsl:choose>
      <xsl:when test="exists(@value)">
        <xsl:value-of select="x:value(@value)"/>
      </xsl:when>
      <xsl:when test="exists(@location)">
        <xsl:value-of select="x:file(@location)"/>
      </xsl:when>
    </xsl:choose>
    <xsl:text></xsl:text>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>
  
  <xsl:template match="property[@file]">
    <xsl:text>Properties.readProperties(</xsl:text>
    <xsl:value-of select="x:value(@file)"/>
    <xsl:text>)</xsl:text>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>
  
  <xsl:template match="property[@environment]" priority="10"/>
  
  <xsl:template match="makeurl">
    <xsl:text>Properties("</xsl:text>
    <xsl:value-of select="@property"/>
    <xsl:text>") = </xsl:text>
    <xsl:value-of select="x:file(@file)"/>
    <xsl:text>.toURI().toASCIIString()</xsl:text>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>
  
  <xsl:template match="basename">
    <xsl:text>Properties("</xsl:text>
    <xsl:value-of select="@property"/>
    <xsl:text>") = </xsl:text>
    <xsl:value-of select="x:file(@file)"/>
    <xsl:text>.getName()</xsl:text>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="dirname">
    <xsl:text>Properties("</xsl:text>
    <xsl:value-of select="@property"/>
    <xsl:text>") = </xsl:text>
    <xsl:value-of select="x:file(@file)"/>
    <xsl:text>.getParent()</xsl:text>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>
  
  
  <xsl:template match="condition">
    <xsl:text>if (</xsl:text>
    <xsl:apply-templates select="*"/>
    <xsl:text>)</xsl:text>
    <xsl:call-template name="x:start-block"></xsl:call-template>
    <xsl:text>Properties("</xsl:text>
    <xsl:value-of select="@property"/>
    <xsl:value-of select="@name"/>
    <xsl:text>") = </xsl:text>
    <xsl:choose>
      <xsl:when test="exists(@value)">
        <xsl:value-of select="x:value(@value)"/>
      </xsl:when>
      <xsl:otherwise>"true"</xsl:otherwise>
    </xsl:choose>
    <xsl:call-template name="x:end-block"/>
    <xsl:if test="@else">
      <xsl:text>else</xsl:text>
      <xsl:call-template name="x:start-block"></xsl:call-template>
      <xsl:text>Properties("</xsl:text>
      <xsl:value-of select="@property"/>
      <xsl:text>") = </xsl:text>
      <xsl:value-of select="x:value(@else)"/>
      <xsl:call-template name="x:end-block"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="target/available" priority="10">
    <xsl:text>if (</xsl:text>
    <xsl:choose>
      <xsl:when test="@file">
        <xsl:value-of select="x:file(@file)"/>
        <xsl:text>.exists()</xsl:text>        
      </xsl:when>
      <xsl:when test="@classname">
        <xsl:text>class_available("</xsl:text>
        <xsl:value-of select="x:value(@classname)"/>
        <xsl:text>")</xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:text>)</xsl:text>
    <xsl:call-template name="x:start-block"/>
    <xsl:text>Properties("</xsl:text>
    <xsl:value-of select="@property"/>
    <xsl:value-of select="@name"/>
    <xsl:text>") = </xsl:text>
    <xsl:choose>
      <xsl:when test="exists(@value)">
        <xsl:value-of select="x:value(@value)"/>
      </xsl:when>
      <xsl:otherwise>"true"</xsl:otherwise>
    </xsl:choose>
    <xsl:call-template name="x:end-block"/>
  </xsl:template>
  
  <xsl:template match="or">
    <xsl:text>(</xsl:text>
    <xsl:for-each select="*">
      <xsl:if test="position() ne 1"> || </xsl:if>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
    <xsl:text>)</xsl:text>
  </xsl:template>
  
  <xsl:template match="and">
    <xsl:text>(</xsl:text>
    <xsl:for-each select="*">
      <xsl:if test="position() ne 1"> &amp;&amp; </xsl:if>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
    <xsl:text>)</xsl:text>
  </xsl:template>
  
  <xsl:template match="not">
    <xsl:if test="empty(isset)">
      <xsl:text>!</xsl:text>
    </xsl:if>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="*"/>
    <xsl:text>)</xsl:text>
  </xsl:template>
  
  <xsl:template match="equals">
    <xsl:choose>
      <xsl:when test="matches(@arg1, '^\$\{.+?list\}$') and @arg2 = ''">
        <xsl:text>job.getSet("</xsl:text>
        <xsl:value-of select="replace(@arg1, '^\$\{(.+?list)\}$', '$1')"/>
        <xsl:text>").isEmpty()</xsl:text>        
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="x:value(@arg1)"/>
        <xsl:text> == </xsl:text>
        <xsl:value-of select="x:value(@arg2)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="istrue">
    <xsl:value-of select="x:value(@value)"/>
    <xsl:text> == "true"</xsl:text>
  </xsl:template>
  
  <xsl:template match="isfalse">
    <xsl:value-of select="x:value(@value)"/>
    <xsl:text> != "true"</xsl:text>
  </xsl:template>
  
  <xsl:template match="isabsolute">
    <xsl:value-of select="x:file(@path)"/>
    <xsl:text>.isAbsolute</xsl:text>
  </xsl:template>
  
  <xsl:template match="os">
    <xsl:text>System.getProperty("os.name") == </xsl:text>
    <xsl:value-of select="x:value(@arch | @family)"/>
  </xsl:template>
  
  <xsl:template match="contains">
    <xsl:value-of select="x:value(@string)"/>
    <xsl:text>.indexOf(</xsl:text>
    <xsl:value-of select="x:value(@substring)"/>
    <xsl:text>) != -1</xsl:text>
  </xsl:template>
  
  <xsl:template match="isset">
    <xsl:text>Properties.contains("</xsl:text>
    <xsl:value-of select="@property"/>
    <xsl:text>")</xsl:text>
  </xsl:template>
  
  <xsl:template match="not/isset">
    <xsl:text>!Properties.contains("</xsl:text>
    <xsl:value-of select="@property"/>
    <xsl:text>")</xsl:text>
  </xsl:template>
  
  <xsl:template match="condition//available[@file]">
    <xsl:value-of select="x:file(@file)"/>
    <xsl:text>.exists()</xsl:text>
  </xsl:template>
  
  <xsl:template match="available[@classname]">
    <xsl:text>class_available(</xsl:text>
    <xsl:value-of select="x:value(@classname)"/>
    <xsl:text>)</xsl:text>
  </xsl:template>
  
  <xsl:function name="x:file" as="xs:string">
    <xsl:param name="value"/>
    <xsl:value-of>
      <xsl:text>new File(</xsl:text>
      <xsl:value-of select="x:value($value)"/>
      <xsl:text>)</xsl:text>
    </xsl:value-of>
  </xsl:function>
    
  <xsl:function name="x:value">
    <xsl:param name="value"/>
    <xsl:variable name="antcall-parameters" select="$value/ancestor-or-self::target[1]/antcall-parameter"/>
    <xsl:variable name="v">
    <xsl:choose>
      <xsl:when test="string-length($value) = 0">""</xsl:when>
      <xsl:otherwise>
        <xsl:variable name="v" as="xs:string*">
          <xsl:analyze-string select="$value" regex="(\$\{{(.+?)\}}|(/))">
            <xsl:matching-substring>
              <xsl:choose>
                <xsl:when test="regex-group(2) = 'file.separator' or regex-group(3) = '/'">
                  <xsl:text>File.separator</xsl:text>
                </xsl:when>
                <xsl:when test="exists($antcall-parameters[@name = regex-group(2)])">
                  <xsl:value-of select="regex-group(2)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:sequence select="concat('Properties(&quot;', regex-group(2), '&quot;)')"/>    
                </xsl:otherwise>
              </xsl:choose>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:if test="string-length(.) gt 0">
                <xsl:sequence select="concat('&quot;', replace(replace(., '&#xA;', '\\n'), '&quot;', '\\&quot;'), '&quot;')"/>
              </xsl:if>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:variable>
        <xsl:for-each select="$v">
          <xsl:if test="position() ne 1"> + </xsl:if>
          <xsl:value-of select="."/>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
    </xsl:variable>
    <xsl:value-of select="replace($v, '\\', '\\\\')"></xsl:value-of>
  </xsl:function>
  
  <xsl:template match="*" priority="-2">
    <xsl:message>No mapping for <xsl:value-of select="name(..)"/>/<xsl:value-of select="name()"/></xsl:message>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template name="x:start-block">
    <xsl:text> {&#xa;</xsl:text>
  </xsl:template>
  
  <xsl:template name="x:end-block">
    <!--xsl:text>&#xa;</xsl:text-->
    <xsl:text>}&#xa;</xsl:text>
  </xsl:template>
  
</xsl:stylesheet>