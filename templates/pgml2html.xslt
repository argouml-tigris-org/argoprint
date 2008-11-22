<?xml version="1.0"?>
<xsl:stylesheet xmlns:UML="org.omg.xmi.namespace.UML" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:svg="http://www.w3.org/2000/svg" version="1.0">

	<xsl:output method="xml" />
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	
	
	
	<xsl:template match="group" mode="usecase">
		<xsl:variable name="desc"><xsl:value-of select="@description"/></xsl:variable>
		<xsl:choose>
			<xsl:when test="starts-with($desc, 'org.argouml.uml.diagram.use_case.ui.FigUseCase')">
				<xsl:variable name="usecaseId"><xsl:value-of select="@href"/></xsl:variable>
				
				<xsl:call-template name="usecase">
					<xsl:with-param name="usecaseId" select="$usecaseId"/>
				</xsl:call-template> 
				
			</xsl:when>
			
			<xsl:when test="starts-with($desc, 'org.argouml.uml.diagram.static_structure.ui.FigClass')">
				<xsl:variable name="classId"><xsl:value-of select="@href"/></xsl:variable>
				<xsl:call-template name="class">
					<xsl:with-param name="classId" select="$classId"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
			
		
		</xsl:choose>
		
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="private">
	
	</xsl:template>
	
	<!-- Convert Use Case -->
	<xsl:template match="/uml/argo//UseCase" mode="usecase" name="usecase">
		<xsl:param name="usecaseId"/>
		
		<xsl:variable name="xmiId" select="./@xmi.id"/>
		UseCase
		xmiId=<xsl:value-of select="./@xmi.id"/>
		usecaseId=<xsl:value-of select="$usecaseId"/>
		name=<xsl:value-of select="@name"/>
		nodename=<xsl:value-of select="name()"/>
		<xsl:for-each select="/uml//UseCase[@xmi.id=$usecaseId]"> 
			<h3><xsl:value-of select="@name"/></h3>
			<p><xsl:value-of select="ModelElement.taggedValue/TaggedValue/TaggedValue.dataValue"/></p>
		</xsl:for-each>
	</xsl:template>
	
	
	<!-- Convert Class -->
	<xsl:template match="Class" name="class">
		<xsl:param name="classId"/>
		<xsl:variable name="xmiId" select="./@xmi.id"/>
		xmiId=<xsl:value-of select="@xmi.id"/>
		classId=<xsl:value-of select="$classId"/>
		name=<xsl:value-of select="@name"/>
		<xsl:if test="$xmiId=$classId">
			<h3><xsl:value-of select="@name"/></h3>
			<p><xsl:value-of select="ModelElement.taggedValue/TaggedValue/TaggedValue.dataValue"/></p>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>


</xsl:stylesheet>
