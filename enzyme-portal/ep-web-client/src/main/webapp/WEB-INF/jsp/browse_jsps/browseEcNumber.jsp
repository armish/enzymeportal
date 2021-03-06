<%-- 
    Document   : browseEcNumber
    Created on : Sep 11, 2013, 12:02:17 PM
    Author     : joseph
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib  prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="xchars" uri="http://www.ebi.ac.uk/xchars"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="Fn" uri="/WEB-INF/epTagLibray.tld" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>



<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
    <head>
        <!--        <meta charset="utf-8">-->

        <!-- Use the .htaccess and remove these lines to avoid edge case issues.
             More info: h5bp.com/b/378 -->
        <!-- <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> -->	<!-- Not yet implemented -->

        <title>Search Result  &lt; Enzyme Portal &gt; &lt; EMBL-EBI</title>
        <meta name="description" content="EMBL-EBI"><!-- Describe what this page is about -->
        <meta name="keywords" content="bioinformatics, europe, institute"><!-- A few keywords that relate to the content of THIS PAGE (not the whol project) -->
        <meta name="author" content="EMBL-EBI"><!-- Your [project-name] here -->

        <!-- Mobile viewport optimized: j.mp/bplateviewport -->
        <meta name="viewport" content="width=device-width,initial-scale=1">


        <link href="${pageContext.request.contextPath}/resources/css/search.css" type="text/css" rel="stylesheet" />
        <!--           <link rel="stylesheet" href="resources/css/enzyme-portal-colours.css" type="text/css" media="screen" />-->
        <link rel="stylesheet" href="resources/css/embl-petrol-colours.css" type="text/css" media="screen" />

        <!--        for production-->
        <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/css/compliance/mini/ebi-fluid-embl.css">

        <!--        javascript was placed here for auto complete otherwise should be place at the bottom for faster page loading-->

<!--        <script src="resources/lib/spineconcept/javascript/jquery-1.5.1.min.js" type="text/javascript"></script>-->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script src="resources/lib/spineconcept/javascript/identification.js" type="text/javascript"></script>
        <script src="resources/javascript/search.js" type="text/javascript"></script>


        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
        <script src="http://yui.yahooapis.com/3.4.1/build/yui/yui-min.js"></script>



        <!-- Full build -->
        <!-- <script src="//www.ebi.ac.uk/web_guidelines/js/libs/modernizr.minified.2.1.6.js"></script> -->

        <!-- custom build (lacks most of the "advanced" HTML5 support -->
        <script src="//www.ebi.ac.uk/ebisearch/examples/ebisearch-globalSearch-template_files/modernizr.js"></script>		

        <!--<! --------------------------------
        GLOBAL SEARCH TEMPLATE - START
       -------------------------------- >-->

        <script type="text/javascript" src="//www.ebi.ac.uk/ebisearch/examples/ebisearch-globalSearch-template_files/jquery-1.8.0.min.js"></script>
        <script type="text/javascript" src="//www.ebi.ac.uk/ebisearch/examples/ebisearch-globalSearch-template_files/jquery-ui-1.8.23.custom.min.js"></script>

        <!--<! --------------------------------
        GLOBAL SEARCH TEMPLATE - END
       -------------------------------- >-->




        <!--        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
                <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
                <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
                
                <script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
        <script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>-->

        <script src="resources/javascript/jqTree/jquery.min.js" type="text/javascript"></script>
        <script src="resources/javascript/jqTree/tree.jquery.js" type="text/javascript"></script>
        <link href="resources/javascript/jqTree/jqtree.css" type="text/css" rel="stylesheet" />


        <!--        <link href="resources/javascript/jsTree/themes/default/style.css" type="text/css" rel="stylesheet" />-->
        <!--        <script src="resources/javascript/jsTree/_lib/jquery.js"></script>-->

        <!--        <script src="resources/javascript/jsTree/jquery.jstree.js"></script>
                <script src="resources/javascript/jsTree/_lib/jquery.cookie.js"></script>
                <script src="resources/javascript/jsTree/_lib/jquery.hotkeys.js"></script>-->

        <!--<script src="resources/javascript/jsTree/jstree.min.js"></script>
        -->





    </head>

    <body class="level2"><!-- add any of your classes or IDs -->
        <div id="skip-to">
            <ul>
                <li><a href="#content">Skip to main content</a></li>
                <li><a href="#local-nav">Skip to local navigation</a></li>
                <li><a href="#global-nav">Skip to EBI global navigation menu</a></li>
                <li><a href="#global-nav-expanded">Skip to expanded EBI global navigation menu (includes all sub-sections)</a></li>
            </ul>
        </div>

        <div id="wrapper" class="container_24">
            <header>
                <div id="global-masthead" class="masthead grid_24">
                    <!--This has to be one line and no newline characters-->
                    <a href="/" title="Go to the EMBL-EBI homepage"><img src="//www.ebi.ac.uk/web_guidelines/images/logos/EMBL-EBI/EMBL_EBI_Logo_white.png" alt="EMBL European Bioinformatics Institute"></a>

                    <nav>
                        <ul id="global-nav">
                            <!-- set active class as appropriate -->
                            <li class="first active" id="services"><a href="/services">Services</a></li>
                            <li id="research"><a href="/research">Research</a></li>
                            <li id="training"><a href="/training">Training</a></li>
                            <li id="industry"><a href="/industry">Industry</a></li>
                            <li id="about" class="last"><a href="/about">About us</a></li>
                        </ul>
                    </nav>

                </div>

                <div id="local-masthead" class="masthead grid_24 nomenu">

                    <!-- local-title -->

                    <div id="local-title" class="grid_12 alpha logo-title"> 
                        <a href="/enzymeportal" title="Back to Enzyme Portal homepage">
                            <img src="${pageContext.request.contextPath}/resources/images/enzymeportal_logo.png" alt="Enzyme Portal logo" style="width :64px;height: 64px; margin-right: 0px">
                        </a> <span style="margin-top: 30px"><h1 style="padding-left: 0px">Enzyme Portal</h1></span> </div>



                    <div class="grid_12 omega">




                        <%@ include file="frontierSearchBox.jsp" %>



                    </div>


                    <nav>
                        <ul class="grid_24" id="local-nav">
                            <li  class="first"><a href="/enzymeportal" title="">Home</a></li>
                            <!--					<li><a href="#">Documentation</a></li>-->
                            <li><a href="faq" title="Frequently Asked questions">FAQ</a></li>
                            <li><a href="about" title="About Enzyme Portal">About Enzyme Portal</a></li>
                            <li class="last active"><a href="${pageContext.request.contextPath}/browseEcNumber" title="Browse Enzyme">Browse Enzyme</a></li>
                            <!-- If you need to include functional (as opposed to purely navigational) links in your local menu,
                                 add them here, and give them a class of "functional". Remember: you'll need a class of "last" for
                                 whichever one will show up last... 
                                 For example: -->
                            <!--					<li class="functional last"><a href="#" class="icon icon-functional" data-icon="l">Login</a></li>-->
                            <li class="functional"><a href="http://www.ebi.ac.uk/support/index.php?query=Enzyme+portal&referrer=http://www.ebi.ac.uk/enzymeportal/" class="icon icon-static" data-icon="f">Feedback</a></li>
                            <!--                            <li class="functional"><a href="#" class="icon icon-functional" data-icon="r">Share</a></li>-->
                            <li class="functional"> <a href="https://twitter.com/share" class="icon icon-functional" data-icon="r" data-dnt="true" data-count="none" data-via="twitterapi">Share</a></li>

                        </ul>
                    </nav>
                </div>
            </header>
            <div id="content" role="main" class="grid_24 clearfix">

                <div class="grid_24">
                    <h3 style="text-align: center; margin-right: 10em">Browse Enzymes By EC classification</h3><br/>
                    <div class="grid_6" style="margin-left: 30em">
                        
<div style="text-align: center; min-width: 170px">
	<div style="text-align: left; margin-left: auto; margin-right: auto; width: 170px">
	   <ul style="list-style-type: none; padding-left: 5px; margin-left: 0px">
		    <li><a href="ecnumber?ec=1&amp;ecname=Oxidoreductases">EC 1</a>&nbsp;&nbsp;Oxidoreductases</li>
			<li><a href="ecnumber?ec=2&amp;ecname=Transferases">EC 2</a>&nbsp;&nbsp;Transferases</li>
			<li><a href="ecnumber?ec=3&amp;ecname=Hydrolases">EC 3</a>&nbsp;&nbsp;Hydrolases</li>
			<li><a href="ecnumber?ec=4&amp;ecname=Lyases">EC 4</a>&nbsp;&nbsp;Lyases</li>
			<li><a href="ecnumber?ec=5&amp;ecname=Isomerases">EC 5</a>&nbsp;&nbsp;Isomerases</li>
			<li><a href="ecnumber?ec=6&amp;ecname=Ligases">EC 6</a>&nbsp;&nbsp;Ligases</li>
		</ul>
	</div>
</div>

                    </div>
                </div>

            </div>













            <footer>
                <!-- Optional local footer (insert citation / project-specific copyright / etc here -->
                <!--
    <div id="local-footer" class="grid_24 clearfix">
                        <p>How to reference this page: ...</p>
                </div>
                -->
                <!-- End optional local footer -->

                <div id="global-footer" class="grid_24">

                    <nav id="global-nav-expanded">

                        <div class="grid_4 alpha">
                            <h3 class="embl-ebi"><a href="/" title="EMBL-EBI">EMBL-EBI</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="services"><a href="/services">Services</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="research"><a href="/research">Research</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="training"><a href="/training">Training</a></h3>
                        </div>

                        <div class="grid_4">
                            <h3 class="industry"><a href="/industry">Industry</a></h3>
                        </div>

                        <div class="grid_4 omega">
                            <h3 class="about"><a href="/about">About us</a></h3>
                        </div>

                    </nav>

                    <section id="ebi-footer-meta">
                        <p class="address">EMBL-EBI, Wellcome Trust Genome Campus, Hinxton, Cambridgeshire, CB10 1SD, UK &nbsp; &nbsp; +44 (0)1223 49 44 44</p>
                        <p class="legal">Copyright &copy; EMBL-EBI 2012 | EBI is an Outstation of the <a href="http://www.embl.org">European Molecular Biology Laboratory</a> | <a href="/about/privacy">Privacy</a> | <a href="/about/cookies">Cookies</a> | <a href="/about/terms-of-use">Terms of use</a></p>	
                    </section>

                </div>

            </footer>
        </div> <!--! end of #wrapper -->


        <!-- JavaScript at the bottom for fast page loading -->
        <c:set var="jfile1" value="${jfile}"></c:set>



        <!--
        
                <script type="text/javascript">
                    $(document).ready(function(){
        
                        //var theData = formatData(${jfile});
                        var theData = ${jfile};
                        console.log(theData);
                        //var jData = ${jfile};
                       // console.log(jData);
                        //data1 = $.parseJSON("[" + data1.contents + "]");
                        //var jsonData = $.parseJSON(theData);
                        //console.log(jsonData);
                       // alert(jsonData);
                        $(function () {
                            $("#demo").jstree({
                                "json_data" : {
                        
        
                                 // "data" : theData,
                                   
                                    
        "data":[{"data":"1","title":"some title","children":[{"data":"1.1","title":"Acting on the CH-OH Group of Donors","desc":"\n    This subclass contains all"}]}],
                                    //,
                                    //"metadata" : "jstree, name, object, etc",
        
                                    "ajax" : {
                                        //cache:false,
                                        "url" : '<%=request.getContextPath()%>/browseEcNumber/?svc=.',
                                        'type': 'POST',
                                        //"data":theData
                                        "data" : function (n) {
                                            
                                        alert(n); 
                                        return { id : n.data ? n.attr("data") : 0 }; 
                                                                }
        
        
        
                                    }
                                },
                                          'progressive_render': true,
                    'progressive_unload': false, 
                                
                                "themes" : {
                                    "theme" : "apple",
                                    "dots" : true,
                                    "icons" : true
                                },
                                "plugins" : [ "themes", "json_data", "ui" ]
                               
                            }).bind('select_node.jstree',function (event, data) {
            var svc=data.inst.get_text(data.rslt.obj); // NODE TEXT
             alert(svc);
        
              
               var dest = [];
          var url = "<%=request.getContextPath()%>/browseEcNumber/?svc=" + svc;
          $.getJSON(url, function(data){
              console.log(data);
              $.each(data, function(k, v) { 
              $.each(v, function(index, obj) { 
                 dest.push ([ obj.dt, obj.tot_count ]);
                 console.log(dest);
              });
               });
        
             }); 
              
              
              
                            });
        
                            /////start bind/////
        //                    .bind(
        //                    "select_node.jstree", function(event, data){
        //                        //selected node object: data.inst.get_json()[0];
        //                        //selected node text: data.inst.get_json()[0].data
        //                       // alert(data.inst.get_text());
        //                        //event.preventDefault();
        //                         //alert(data.inst.get_json()[0]);
        //                        //console.log(data.inst.get_json()[0].data);
        //                        //alert(data.attr("data"));
        //           
        //                        
        //                        
        //                        
        //                        
        //                        
        //                    }
        //                )
        //                    // prevent the default event of the link
        //
        //                    .delegate("a", "click", function (event, data) { event.preventDefault(); });
                            
                            //////////////end delegate
        
                            
                            
                        });
                        
                        
                        
                        
                                
                        function formatData(arr) {
                            //console.log(arr)
                            var label, data = [],
                            obj, node;
                            if (!$.isArray(arr)) {
                                arr = [arr];
                            }
                            //console.log(arr);
                            var l = arr.length,
                            i;
                
                            for (i = 0; i < l; ++i) {
                                node = {};
                                obj = arr[i];
                                //console.log(node);
                                //console.log("obj ec data == "+obj.ec);
                                //console.log("obj name data == "+obj.name);
                                //node.label = obj.user.screenName + ": " + obj.tweetText + " (" + obj.createdDate + ")";
                                //node.label = "Ec "+ obj.ec +"\n"+ obj.name;
                                node.data = obj.ec;
                                //node.data ="EC "+ obj.ec;
        //                        node.attr = node.data;
                                node.title = obj.name;
        //                        node.description = obj.description;
                
                       
                                if (typeof obj.subclasses == "object") { //fetch the children
                                      node.children = formatData(obj.subclasses);
                                    //node.attribute = obj.subclasses;
                                     //node.metadata = formatData(obj.subclasses);
                                }
                        
                               
                                if (typeof obj.subsubclasses == "object") { //fetch the children
                                    //node.label = obj.subclasses;
                                    node.children = formatData(obj.subsubclasses);
                                }
                        
                                if (typeof obj.entries == "object") { //fetch the children
                                    //node.label = obj.subclasses;
                                    node.children = formatData(obj.entries);
                                }
                                data.push(node);
                       //console.log(data);
                            }
                    
                
                            return data;
                     
                        }
                    
                    
        
            
            
            
                    });        
                    
                    
                
                    
                    
                    
                    
                </script>      
        
        
        
        
        -->




        <script>
            $(document).ready(function() {
                console.log(${jfile});
                //alert(${jfile});
                var jdata = ${jfile};
      
                //        $("#tree1").tree({
                //     data: jdata
                //     
                //     //data: [{"label":"ec 6.6","children":[{"label":"ec 6.6.1","id":"6.6.1","title":"Forming coordination complexes","desc":""}]}]
                //
                //  }).bind('tree.click', function(event) {
                //    var node = event.node;
                //    console.log("param sent "+ node.id);
                //    
                ////    //var nodes = $('#tree1').tree('getNodeById', node.id);
                ////console.log("this is the node "+ node);
                //$('#tree1').tree('loadDataFromUrl', '<%=request.getContextPath()%>/browseEcNumberJson/?svc='+node.id, node);
                //
                //    
                //   
                //     });


                $('#tree1').tree({
                    data: jdata,
                    
//                    onCreateLi: function(node, $li) {
//                        // Add 'icon' span before title
//                        console.log("oncreate "+ node.name);
//                       // $li.find('.jqtree-title').before('<span class="icon"></span>');
//                        
//                    $li.find('.jqtree-element').append(
//                                '<a href="#node-'+ node.id +'" class="edit" data-node-id="'+
//                               node.id +'">edit</a>' )    
//                    },
                    
                    //autoOpen: true,
                    dragAndDrop: false,
                    onCanSelectNode: function(node) {
                        if (node.children.length == 0) {
                            // Nodes without children can be selected
                            return true;
                        }
                        else {
                            // Nodes with children cannot be selected
                            return false;
                        }
                    }
                }).bind(
                'tree.select',
                function processUrlData(e) {
                    var node = e.node;
                    console.log("param sent "+ node.id);

                    $.getJSON(
                    '<%=request.getContextPath()%>/browseEcNumberJson/?svc='+node.id,
                    function(data) {
             
                        console.log("AJAX"+ node);
                        //$tree.tree('loadData', data, node);
                        //$('#tree1').tree('toJson');
                       var parent_node = $('#tree1').tree('getNodeById', node.id);

                       var level = parent_node.getData();
                       var nodes = $('#tree1').tree('getNodeById', node.id);
                       console.log(node.children);
                      
                       // var nodes = $('#tree1').tree('getNodeById', node.id);
                        //$('#tree1').tree('removeNode', nodes);
                        
                        $('#tree1').tree('loadData', data,node);
                        
                    }
                );

                    //   $.ajax({
                    //        type: "GET",
                    //        url: '<%=request.getContextPath()%>/browseEcNumberJson/?svc='+node.id,
                    //        
                    //        //dataType: "json",
                    //        success: function(responseData, textStatus) {
                    //            console.log(textStatus);
                    //            console.log(responseData);
                    //            $('#tree1').tree('loadData', responseData,node);
                    //        },
                    //        error : function(responseData) {
                    //            consoleDebug("  in ajax, error: " + responseData.responseText); 
                    //        }
                    //    });
            
                }
            );



   

      
  
        
        
        
            });
        </script>


        <!--                <script>
                              
                //alert('${jfile1}');       
                var jdata = ${jfile};  //[{"ec":"1.1.1.1","name":"alcohol dehydrogenase","label":"first","children":"firstChild"}];
                console.log(jdata);
        //        var d;
        //        $('#tree1').tree({
        //            data: jdata,
        //            autoOpen: true,
        //            dragAndDrop: true
        //        });
        //        //Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --disable-web-security
        //        $.getJSON(
        //            'http://www.ebi.ac.uk/intenz/ws/EC/1.1.1.1.json',
        //        
        //            function(data) {
        //                $('#tree1').tree({
        //                    data: data
        //                    //dataUrl:'http://www.ebi.ac.uk/intenz/ws/EC/1,1,1,1.json'
        //                });
        //            }
        //        );
                  
                $.getJSON('http://whateverorigin.org/get?url=' + encodeURIComponent('http://www.ebi.ac.uk/intenz/ws/EC/.json') + '&callback=?', 
                function(data1){
                        //alert(data1.contents);
                       // d = data1.contents
                        //data1 = $.parseJSON("[" + data1.contents + "]");
                         //data1 = $.parseJSON( data1.contents );
                       var treeData = ${jfile}; //formatData(data1);
                       
                         $('#tree1').tree({
                            //dataUrl:data1.status.url,
                            data: treeData,
                             //openedIcon: '-',
                             //closedIcon: '+',
                            autoOpen: false,
                            saveState: true
                            //selectable: true,
                //    onCanSelectNode: function(node) {
                //        if (node.children.length == 0) {
                //            // Nodes without children can be selected
                //            return true;
                //        }
                //        else {
                //            // Nodes with children cannot be selected
                //            return false;
                //        }
                //    }
                        }).bind('tree.select', function(event) {
                                   // The clicked node is 'event.node'
                        var node = event.node; 
                       
           console.log("param sent "+ node.id);
            
           //var nodes = $('#tree1').tree('getNodeById', node.id);
        console.log("this is the node "+ node);
        $('#tree1').tree('loadDataFromUrl', '<%=request.getContextPath()%>/browseEcNumberJson/?svc='+node.id, node);
                        });
                        
                        
                    
                    
                });
                
                
                
                
                function formatData(arr) {
                    //console.log(arr)
                    var label, data = [],
                        obj, node;
                    if (!$.isArray(arr)) {
                        arr = [arr];
                    }
                    console.log(arr);
                    var l = arr.length,
                        i;
                
                    for (i = 0; i < l; ++i) {
                        node = {};
                        obj = arr[i];
                        //console.log(obj);
                        //node.label = obj.user.screenName + ": " + obj.tweetText + " (" + obj.createdDate + ")";
                        node.label = "Ec "+ obj.ec +"\t"+ obj.name;
                        //node.label = obj.ec;
                
                       
                        if (typeof obj.subclasses == "object") { //fetch the children
                            //node.label = obj.subclasses;
                            node.children = formatData(obj.subclasses);
                        }
                        
                               
                        if (typeof obj.subsubclasses == "object") { //fetch the children
                            //node.label = obj.subclasses;
                            node.children = formatData(obj.subsubclasses);
                        }
                        
                         if (typeof obj.entries == "object") { //fetch the children
                            //node.label = obj.subclasses;
                            node.children = formatData(obj.entries);
                        }
                        data.push(node);
                       
                    }
                    
                
                    return data;
                     
                }
                
        
        //        
        //            /**
        //             * make a request to intenz
        //             */
        //            function requestData(ecNumber) {
        //               $.getJSON('http://whateverorigin.org/get?url=' + encodeURIComponent('http://www.ebi.ac.uk/intenz/ws/EC/'+ecNumber+'.json') + '&callback=?', 
        //        function(data2){
        //                //alert(data1.contents);
        //               // d = data1.contents
        //                //data1 = $.parseJSON("[" + data1.contents + "]");
        //                 data2 = $.parseJSON( data2.contents );
        //               var treeData2 = formatDataLevel2(data2);
        //            
        //                 $('#tree1').tree({
        //                    //dataUrl:data1.status.url,
        //                    data: treeData2,
        //                    autoOpen: true
        //                    //added to experiment
        //        //                    autoOpen: 1,
        //        //        onCreateLi: function(node, $li) {
        //        //            // Append a link to the jqtree-element div.
        //        //            // The link has an url '#node-[id]' and a data property 'node-id'.
        //        //            $li.find('.jqtree-element').append(
        //        //                '<a href="#node-'+ node.id +'" class="edit" data-node-id="'+
        //        //                node.id +'">edit</a>'
        //        //            );
        //        //        }
        //        
        //                });
        //                
        //        
        //                
        //                
        //                
        //                
        //        }); 
        //            }
        //        
        //        
        //            
                            
                        </script>-->


        <c:if test="${pageContext.request.serverName!='www.ebi.ac.uk'}" >
            <script type="text/javascript">var redline = {}; redline.project_id = 185653108;</script><script id="redline_js" src="http://www.redline.cc/assets/button.js" type="text/javascript">
                
            </script>
            <script>
                $(document).ready(function() {
                    setTimeout(function(){
                        // Handler for .ready() called.
                        $("#redline_side_car").css("background-image","url(resources/images/redline_left_button.png)");
                        $("#redline_side_car").css("background-size", "23px auto");
                        $("#redline_side_car").css("display", "block");
                        $("#redline_side_car").css("width", "23px");
                        $("#redline_side_car").css("height", "63px");
                    },1000);
                });
            </script>
        </c:if>

        <!--        add twitter script for twitterapi-->
        <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="https://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>



        <!--    now the frontier js for ebi global result-->
        <script src="//www.ebi.ac.uk/web_guidelines/js/ebi-global-search-run.js"></script>
        <script src="//www.ebi.ac.uk/web_guidelines/js/ebi-global-search.js"></script>



        <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/cookiebanner.js"></script>  
        <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/foot.js"></script>

        <!-- end scripts-->


        <!-- Change UA-XXXXX-X to be your site's ID -->
        <!--
      <script>
          window._gaq = [['_setAccount','UAXXXXXXXX1'],['_trackPageview'],['_trackPageLoadTime']];
          Modernizr.load({
            load: ('https:' == location.protocol ? '//ssl' : '//www') + '.google-analytics.com/ga.js'
          });
        </script>
        -->


        <!-- Prompt IE 6 users to install Chrome Frame. Remove this if you want to support IE 6.
             chromium.org/developers/how-tos/chrome-frame-getting-started -->
        <!--[if lt IE 7 ]>
            <script src="//ajax.googleapis.com/ajax/libs/chrome-frame/1.0.3/CFInstall.min.js"></script>
            <script>window.attachEvent('onload',function(){CFInstall.check({mode:'overlay'})})</script>
          <![endif]-->

    </body>

</html>

