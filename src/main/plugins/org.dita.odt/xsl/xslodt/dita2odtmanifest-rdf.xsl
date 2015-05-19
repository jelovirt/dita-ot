<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:ns0="http://docs.oasis-open.org/ns/office/1.2/meta/pkg#"
                version="2.0">
  
  <xsl:template match="/">
    <rdf:RDF>
      <rdf:Description rdf:about="styles.xml">
        <rdf:type rdf:resource="http://docs.oasis-open.org/ns/office/1.2/meta/odf#StylesFile"/>
      </rdf:Description>
      <rdf:Description rdf:about="">
        <ns0:hasPart rdf:resource="styles.xml"/>
      </rdf:Description>
      <rdf:Description rdf:about="content.xml">
        <rdf:type rdf:resource="http://docs.oasis-open.org/ns/office/1.2/meta/odf#ContentFile"/>
      </rdf:Description>
      <rdf:Description rdf:about="">
        <ns0:hasPart rdf:resource="content.xml"/>
      </rdf:Description>
      <rdf:Description rdf:about="">
        <rdf:type rdf:resource="http://docs.oasis-open.org/ns/office/1.2/meta/pkg#Document"/>
      </rdf:Description>
    </rdf:RDF>
  </xsl:template>
  
</xsl:stylesheet>