<?xml version="1.0" encoding="UTF-8" ?>
<!-- This file is part of the DITA Open Toolkit project.
     See the accompanying license.txt file for applicable licenses. -->
<!-- (c) Copyright IBM Corp. 2004, 2005 All Rights Reserved. -->
<!-- 20090904 RDA: Add support for stepsection; combine duplicated logic
                   for main steps and steps-unordered templates. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:anim="urn:oasis:names:tc:opendocument:xmlns:animation:1.0"
  xmlns:smil="urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0"
  xmlns:prodtools="http://www.ibm.com/xmlns/prodtools"
  xmlns:related-links="http://dita-ot.sourceforge.net/ns/200709/related-links"
  xmlns:dita2html="http://dita-ot.sourceforge.net/ns/200801/dita2html"
  xmlns:ditamsg="http://dita-ot.sourceforge.net/ns/200704/ditamsg"
  version="2.0"
  exclude-result-prefixes="xs related-links dita2html ditamsg">

  <!-- Determines whether to generate titles for task sections. Values are YES and NO. -->
  <xsl:param name="GENERATE-TASK-LABELS" select="'NO'"/>

  <!-- == TASK UNIQUE SUBSTRUCTURES == -->

  <xsl:template match="*[contains(@class,' task/taskbody ')]" name="topic.task.taskbody">
    <!-- Added for DITA 1.1 "Shortdesc proposal" -->
    <!-- get the abstract para -->
    <xsl:apply-templates select="preceding-sibling::*[contains(@class,' topic/abstract ')]"
      mode="outofline"/>

    <!-- get the short descr para -->
    <xsl:apply-templates select="preceding-sibling::*[contains(@class,' topic/shortdesc ')]"
      mode="outofline"/>

    <!-- Insert pre-req links here, after shortdesc - unless there is a prereq section about -->
    <xsl:if test="not(*[contains(@class,' task/prereq ')])">
      <xsl:apply-templates select="following-sibling::*[contains(@class,' topic/related-links ')]"
        mode="prereqs"/>
    </xsl:if>
    <xsl:apply-templates/>

  </xsl:template>

  <xsl:template match="*[contains(@class,' task/prereq ')]" mode="get-output-class">p</xsl:template>

  <xsl:template match="*[contains(@class,' task/prereq ')]" name="topic.task.prereq">
    <xsl:apply-templates select="." mode="prereq-fmt"/>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/prereq ')]" mode="prereq-fmt">
    <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
    <xsl:apply-templates select="../following-sibling::*[contains(@class,' topic/related-links ')]" mode="prereqs"/>
  </xsl:template>

  <xsl:template match="*" mode="make-steps-compact" as="xs:boolean">
    <xsl:choose>
      <xsl:when test="*/*[contains(@class,' task/info ')]">true</xsl:when>
      <xsl:when test="*/*[contains(@class,' task/stepxmp ')]">true</xsl:when>
      <xsl:when test="*/*[contains(@class,' task/tutorialinfo ')]">true</xsl:when>
      <xsl:when test="*/*[contains(@class,' task/stepresult ')]">true</xsl:when>
      <xsl:otherwise>false</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/steps ')]" name="topic.task.steps">
    <!-- start flagging -->
    <xsl:apply-templates select="." mode="start-add-odt-flags">
      <xsl:with-param name="family" select="'_list'"/>
    </xsl:apply-templates>    
    <xsl:apply-templates select="." mode="steps-fmt"/>
    <!-- end flagging -->
    <xsl:apply-templates select="." mode="end-add-odt-flags">
      <xsl:with-param name="family" select="'_list'"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/steps ') or contains(@class,' task/steps-unordered ')]"
                mode="common-processing-within-steps">
    <xsl:param name="list-type">
      <xsl:choose>
        <xsl:when test="contains(@class,' task/steps ')">ordered_list_style</xsl:when>
        <xsl:otherwise>list_style</xsl:otherwise>
      </xsl:choose>
    </xsl:param>
    <xsl:apply-templates select="." mode="generate-task-label">
      <xsl:with-param name="use-label">
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'task_procedure'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="*[contains(@class,' task/stepsection ')]"/>
    <text:list text:style-name="{$list-type}">
      <xsl:apply-templates select="*[contains(@class,' task/step ')]">
        <xsl:with-param name="start-value" select="position()" as="xs:integer"/>
      </xsl:apply-templates>
    </text:list>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/stepsection ')]">
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/steps-unordered ')]" name="topic.task.steps-unordered">
    <!-- start flagging -->
    <xsl:apply-templates select="." mode="start-add-odt-flags">
      <xsl:with-param name="family" select="'_list'"/>
    </xsl:apply-templates>
    <!-- render list -->
    <xsl:apply-templates select="." mode="stepsunord-fmt"/>
    <!-- end flagging -->
    <xsl:apply-templates select="." mode="end-add-odt-flags">
      <xsl:with-param name="family" select="'_list'"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/steps ')]" mode="steps-fmt">
    <xsl:apply-templates select="." mode="common-processing-within-steps">
      <xsl:with-param name="list-type" select="'ordered_list_style'"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/steps-unordered ')]" mode="stepsunord-fmt">
    <xsl:apply-templates select="." mode="common-processing-within-steps">
      <xsl:with-param name="list-type" select="'list_style'"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="*[contains(@class, ' task/step ')]">
    <xsl:param name="start-value" as="xs:integer">0</xsl:param>  
    <text:list-item text:start-value="{$start-value}">
      <xsl:apply-templates/>
    </text:list-item>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/substeps ')]" name="topic.task.substeps">
    <text:list text:style-name="ordered_list_style">      
      <xsl:apply-templates/>
    </text:list>
  </xsl:template>

  <!-- nested step -->
  <xsl:template match="*[contains(@class,' task/substep ')]" name="topic.task.substep">
    <text:list-item>
      <xsl:apply-templates/>
    </text:list-item>
  </xsl:template>
  
  <!-- choices contain choice items -->
  <xsl:template match="*[contains(@class,' task/choices ')]" name="topic.task.choices">
    <text:list text:style-name="list_style">
      <xsl:apply-templates/>
    </text:list>
  </xsl:template>
  
  <!-- task choice table -->
  <xsl:template match="*[contains(@class, ' task/choicetable ')]">
    <xsl:variable name="tablenameId" select="generate-id(.)"/>
    <xsl:choose>
      <xsl:when test="not(*[contains(@class,' task/chhead ')])">
        <xsl:apply-templates select="." mode="start-add-odt-flags">
          <xsl:with-param name="family" select="'_table'"/>
        </xsl:apply-templates>
        <table:table table:name="{concat('Table', $tablenameId)}">
          <xsl:apply-templates select="." mode="start-add-odt-flags">
            <xsl:with-param name="family" select="'_table_attr'"/>
          </xsl:apply-templates>
          <xsl:variable name="colnumNum">
            <xsl:call-template name="count_columns_for_simpletable"/>
          </xsl:variable>
          <xsl:call-template name="create_columns_for_simpletable">
            <xsl:with-param name="column" select="$colnumNum"/>
          </xsl:call-template>
          <xsl:call-template name="create_head_for_choicetable"/>
          <xsl:apply-templates/>
        </table:table>
        <!-- end flagging -->
        <xsl:apply-templates select="." mode="end-add-odt-flags">
          <xsl:with-param name="family" select="'_table'"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="create_simpletable"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- create choicetable header. -->
  <xsl:template name="create_head_for_choicetable">
    <table:table-header-rows>
      <table:table-row>
        <table:table-cell office:value-type="string" table:style-name="cell_style_1_task">
          <text:p>
            <text:span text:style-name="bold">
              <xsl:call-template name="getVariable">
                <xsl:with-param name="id" select="'Option'"/>
              </xsl:call-template>
            </text:span>
          </text:p>
        </table:table-cell>
        <table:table-cell office:value-type="string" table:style-name="cell_style_2_task">
          <text:p>
            <text:span text:style-name="bold">
              <xsl:call-template name="getVariable">
                <xsl:with-param name="id" select="'Description'"/>
              </xsl:call-template>
            </text:span>
          </text:p>
        </table:table-cell>
      </table:table-row>
    </table:table-header-rows>
  </xsl:template>

  <xsl:template match="*[contains(@class,' task/chrow ')]" priority="2">
    <table:table-row>
      <xsl:apply-templates mode="emit-cell-style"/>
    </table:table-row>
  </xsl:template>

  <!-- for choption in choice table. -->
  <!--xsl:template match="*[contains(@class, ' task/choption ')]" mode="emit-cell-style">
    <table:table-cell office:value-type="string">
      <xsl:call-template name="create_style_stable"/>
      <text:p>
        <text:span text:style-name="bold">
          <xsl:call-template name="gen_txt_content"/>
        </text:span>
      </text:p>
      <xsl:apply-templates select="*[@class]"/>
    </table:table-cell>
  </xsl:template-->
    
  <xsl:template match="*[contains(@class, ' task/cmd ')]">
    <text:p text:style-name="indent_paragraph_style">
      <xsl:if test="../@importance = 'optional'">
        <text:span text:style-name="bold">
          <xsl:call-template name="getVariable">
            <xsl:with-param name="id" select="'Optional'"/>
          </xsl:call-template>
          <xsl:call-template name="getVariable">
            <xsl:with-param name="id" select="'ColonSymbol'"/>
          </xsl:call-template>
          <xsl:text> </xsl:text>
        </text:span>
      </xsl:if>
      <xsl:if test="../@importance = 'required'">
        <text:span text:style-name="bold">            
          <xsl:call-template name="getVariable">
            <xsl:with-param name="id" select="'Required'"/>
          </xsl:call-template>
          <xsl:call-template name="getVariable">
            <xsl:with-param name="id" select="'ColonSymbol'"/>
          </xsl:call-template>
          <xsl:text> </xsl:text>
        </text:span>
      </xsl:if>
      <text:span>
        <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
        <xsl:apply-templates/>
        <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
      </text:span>
    </text:p>
  </xsl:template>

  <xsl:template match="*[contains(@class, ' task/stepresult ')]">
    <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' task/info ')]" name="topic.task.info">
    <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' task/tutorialinfo ')]" name="topic.task.tutorialinfo">
    <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
  </xsl:template>
  
  <!-- these para-like items need a leading space -->
  <xsl:template match="*[contains(@class,' task/stepxmp ')]" name="topic.task.stepxmp">
    <xsl:apply-templates select="." mode="start-add-odt-revflags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-revflags"/>
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' task/context ')]">
    <xsl:apply-templates select="." mode="generate-task-label">
      <xsl:with-param name="use-label">
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'task_context'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>	
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' task/result ')]">
    <xsl:apply-templates select="." mode="generate-task-label">
      <xsl:with-param name="use-label">
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'task_results'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>	
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' task/postreq ')]">
    <xsl:apply-templates select="." mode="generate-task-label">
      <xsl:with-param name="use-label">
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'task_postreq'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>	
  </xsl:template>
  
  <xsl:template match="*[contains(@class,' task/taskbody ')]/*[contains(@class,' topic/example ')][not(*[contains(@class,' topic/title ')])]">
    <xsl:apply-templates select="." mode="generate-task-label">
      <xsl:with-param name="use-label">
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'task_example'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="." mode="start-add-odt-flags"/>
    <xsl:apply-templates/>
    <xsl:apply-templates select="." mode="end-add-odt-flags"/>
  </xsl:template>
  
  <xsl:template match="*" mode="generate-task-label">
    <xsl:param name="use-label"/>
    <xsl:if test="$GENERATE-TASK-LABELS='YES'">
      <xsl:variable name="headLevel" select="count(ancestor::*[contains(@class,' topic/topic ')])+1"/>
      <text:p text:style-name="{concat('Heading_20_', $headLevel)}">
        <xsl:value-of select="$use-label"/>
      </text:p>
      <!-- 
      <div class="tasklabel">
        <xsl:element name="{$headLevel}">
          <xsl:attribute name="class">sectiontitle tasklabel</xsl:attribute>
          <xsl:value-of select="$use-label"/>
        </xsl:element>
      </div>
      -->
    </xsl:if>
  </xsl:template>
  
  <!-- Related links -->
  <!-- Tasks have their own group. -->
  <xsl:template match="*[contains(@class, ' topic/link ')][@type='task']" mode="related-links:get-group" name="related-links:group.task">
    <xsl:text>task</xsl:text>
  </xsl:template>

  <!-- Priority of task group. -->
  <xsl:template match="*[contains(@class, ' topic/link ')][@type='task']" mode="related-links:get-group-priority" name="related-links:group-priority.task">
    <xsl:value-of select="2"/>
  </xsl:template>

  <xsl:template match="*[contains(@class, ' topic/link ')][@type='task']" mode="related-links:result-group" name="related-links:result.task">
    <xsl:param name="links"/>
    <xsl:variable name="samefile">
      <xsl:call-template name="check_file_location"/>
    </xsl:variable>
    <xsl:variable name="href-value">
      <xsl:call-template name="format_href_value"/>
    </xsl:variable>
    <text:p>
      <text:span text:style-name="bold">        
        <xsl:call-template name="getVariable">
          <xsl:with-param name="id" select="'Related tasks'"/>
        </xsl:call-template>
      </text:span>
    </text:p>
    <text:p>
      <xsl:call-template name="create_related_links">
        <xsl:with-param name="samefile" select="$samefile"/>
        <xsl:with-param name="href-value" select="$href-value"/>
      </xsl:call-template>
    </text:p>
  </xsl:template>

</xsl:stylesheet>