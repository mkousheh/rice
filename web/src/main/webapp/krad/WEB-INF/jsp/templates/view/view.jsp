<%--

    Copyright 2005-2012 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="view" classname="org.kuali.rice.krad.uif.view.View"/>

<!-- VIEW -->
<krad:div component="${view}">

  <!-- BREADCRUMBS -->
  <c:if test="${!view.breadcrumbsInApplicationHeader}">
    <krad:template component="${view.breadcrumbs}"/>
  </c:if>

  <!-- VIEW HEADER -->
  <krad:template component="${view.header}"/>

  <!-- VIEW CONTENT -->
  <div id="Uif-ViewContentWrapper" class="uif-viewContentWrapper">

    <!-- VIEW NAVIGATION -->
    <div>
      <krad:template component="${view.navigation}" currentPageId="${view.currentPageId}"/>
    </div>

    <!-- PAGE CONTENT -->
    <div id="Uif-PageContentWrapper" class="uif-pageContentWrapper">
      <krad:template component="${view.currentPage}"/>
    </div>

  </div>

  <!-- VIEW FOOTER -->
  <div id="viewfooter_div">
    <krad:template component="${view.footer}"/>
  </div>
</krad:div>
