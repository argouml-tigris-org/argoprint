<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:UML="org.omg.xmi.namespace.UML">

<xsl:template match="/">
 <html><head><title>XMI-based XSLT evaluation</title></head>
 <body>

  <h1>Chapter 1</h1>
  <p>Shows a subset of classes based on the name of the containing package. For each of these classes, shows all the names and types of the attributes. For those classes that doesn't have attributes include instead the text "No attributes."</p>
  <xsl:apply-templates select="descendant::UML:Package" />

  <h1>Chapter 2</h1>
  <p>Shows a subset of classes based on the name of one of the stereotypes. For each of these classes, shows the signature of all the operations including return value, direction, name, and type of each parameter. Each method as a bullet in a bulleted list. </p>
<!--   Don't select the object references -->
  <xsl:apply-templates select="descendant::UML:Stereotype[@xmi.id]" />

  <h1>Chapter 3</h1>
 <p>Show a subset of classes based on the existance of a parameter named "something" in any of the operations in the class. For each of these classes, if they contain operations other than the one named "something", show these in a table with one operation on each line with the operation name in the first column and then one column per distinct type for all parameters with the names of the parameters filled in the table. For those classes that doesn't have any other operations, include instead the text "No other operations."</p>  <xsl:apply-templates
  select="descendant::UML:Class[@xmi.id and UML:Classifier.feature/UML:Operation/UML:BehavioralFeature.parameter/UML:Parameter[@name='something']]" />

  <h1>Chapter 4</h1>
  <p>Show all use case diagrams. Under each use case diagram, a list of actors and a list of use cases both with their explanations.</p>
  <p><i>Note: Must figure out how to use diagram info in .zargo files first.</i></p>
  
  <h1>Chapter 5</h1>
  <p>Show the use cases that are linked to a requirement (requirements fetched from some other tool than argouml indexed on the use case names). Under each use case, show the requirement text.</p>
  <xsl:apply-templates select="descendant::UML:UseCase[@xmi.id]" />

 </body>
 </html>
</xsl:template>


<xsl:template match="UML:Package">
<!--   Must be improved to display the complete name of nested packages -->

  <h2><xsl:text>Package </xsl:text><xsl:value-of select="@name" /></h2>
  <xsl:for-each select="UML:Namespace.ownedElement/UML:Class">
   <h3><xsl:text>Class </xsl:text><xsl:value-of select="@name" /></h3>
   <xsl:choose>
    <xsl:when test="UML:Classifier.feature/UML:Attribute">
     <ul>
     <xsl:for-each select="UML:Classifier.feature/UML:Attribute">
      <li><xsl:value-of select="@name" />:
       <xsl:variable name="ref" select="UML:StructuralFeature.type/*/@xmi.idref" />
<!--        Looks for the referenced object - id() function does not work -->
<!--        here because of the DTD vs. namespace issue -->
       <xsl:value-of select="/descendant::*[@xmi.id=$ref]/@name" />
      </li>
     </xsl:for-each>
     </ul>
    </xsl:when>

    <xsl:otherwise>
     <xsl:text>No attributes</xsl:text>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template match="UML:Stereotype">
  <h2><xsl:text>Stereotype </xsl:text><xsl:value-of select="@name" /></h2>

  <xsl:variable name="cid" select="@xmi.id"/>  
<!--   Select UML:Class objects that refer the current stereotype -->
  <xsl:for-each select="/descendant::*[name()='UML:Class' and descendant::*[@xmi.idref=$cid]]">
   <h3><xsl:text>Class </xsl:text><xsl:value-of select="@name" /></h3>

   <ul>
   <xsl:for-each select="UML:Classifier.feature/UML:Operation">
    <li>
     <!-- Method name -->
     <xsl:value-of select="@name" />:

     <!-- Return type -->
     <xsl:variable
      name="cid"
      select="UML:BehavioralFeature.parameter/UML:Parameter[@name='return']
      /UML:Parameter.type/*/@xmi.idref"/>
     <!-- Note: IDs are supposed to be unique so a single element would be selected here -->
     <xsl:value-of select="/descendant::*[@xmi.id=$cid]/@name"/>

     <!-- Parameters -->
     <ul>
     <xsl:for-each select="UML:BehavioralFeature.parameter/UML:Parameter[not(@name='return')]">
      <xsl:variable 
       name="cid" select="UML:Parameter.type/*/@xmi.idref" />

       <li><xsl:value-of select="@name" />:
       <xsl:value-of select="/descendant::*[@xmi.id=$cid]/@name" /></li>
     </xsl:for-each>
     </ul>
         </li>
   </xsl:for-each>
   </ul>
  </xsl:for-each>
</xsl:template>

<xsl:template match="UML:Class">
 <h2><xsl:text>Class </xsl:text><xsl:value-of select="@name" /></h2>

 <xsl:choose>
  <!-- If other methods exist besides the one containing something. -->
  <xsl:when
    test="UML:Classifier.feature/UML:Operation
    [not(UML:BehavioralFeature.parameter/UML:Parameter[@name='something'])]">

    <table border="1">

   <!-- Operations that don't have something as parameter -->
   <xsl:variable
    name="otherops"
    select="UML:Classifier.feature/UML:Operation
    [not(UML:BehavioralFeature.parameter/UML:Parameter[@name='something'])]" />

   <!-- Unique type ids -->   
   <xsl:variable
    name="typeids"
    select="$otherops
    /UML:BehavioralFeature.parameter/UML:Parameter[not(@name='return')]/UML:Parameter.type/*
    /@xmi.idref"/>

   <!-- Headers -->
   <tr><td><xsl:text>Operation</xsl:text></td>

   <xsl:for-each select="$typeids">
     <xsl:variable name="currenttype" select="."/>
     <td><xsl:value-of select="/descendant::*[@xmi.id=$currenttype]/@name" /></td>
   </xsl:for-each>

   </tr>

   <!-- Table Body -->
   <xsl:for-each select="$otherops">
    <xsl:variable name="currentop" select="." />

    <tr><td><xsl:value-of select="$currentop/@name"/></td>

    <xsl:for-each select="$typeids">
     <xsl:variable name="currenttype" select="." />
     
     <td>
     <xsl:for-each
       select="$currentop/UML:BehavioralFeature.parameter/UML:Parameter
       [UML:Parameter.type/*[@xmi.idref=$currenttype]]/@name">
       <xsl:value-of select="." />
     </xsl:for-each>
     </td>

    </xsl:for-each>

    </tr>
    
   </xsl:for-each>

   </table>
  </xsl:when>

  <!-- If the one containing something is the only one -->
  <xsl:otherwise>
   <xsl:text>No other operations</xsl:text>
  </xsl:otherwise>
 </xsl:choose>
</xsl:template>

<xsl:template match="UML:UseCase">
  <!--  This part depends on how is the requirements input specified, assumig fixed -->
 <xsl:variable name="ucname" select="@name"/>
 <xsl:if test="@name=document('req.xml')/argoreq/requirement/@name">
  <h2><xsl:text>UseCase </xsl:text><xsl:value-of select="@name"/></h2>
  <p>
   <xsl:text>Requirement:</xsl:text>
   <xsl:value-of select="document('req.xml')/argoreq/requirement[@name=$ucname]/@text"/>
  </p>
 </xsl:if>
</xsl:template>

</xsl:stylesheet>