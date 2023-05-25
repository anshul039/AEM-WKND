/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.sling.scripting.sightly.apps.wknd.components.rating;

import java.io.PrintWriter;
import java.util.Collection;
import javax.script.Bindings;

import org.apache.sling.scripting.sightly.render.RenderUnit;
import org.apache.sling.scripting.sightly.render.RenderContext;

public final class rating__002e__html extends RenderUnit {

    @Override
    protected final void render(PrintWriter out,
                                Bindings bindings,
                                Bindings arguments,
                                RenderContext renderContext) {
// Main Template Body -----------------------------------------------------------------------------

out.write("<html>\n<body>\n<div>\n\t<p> Did you like the page?</p>\n\t<input type=\"radio\" id=\"yes\" name=\"page_rating\" onclick=\"handleChange(this);\" value=\"Y\"/>\n    <label for=\"yes\">Yes</label><br/>\n    <input type=\"radio\" id=\"no\" name=\"page_rating\" onclick=\"handleChange(this);\" value=\"N\"/>\n    <label for=\"no\">No</label><br/>\n</div>\n<script async src=\"https://www.googletagmanager.com/gtag/js?id=UA-72067721-1\"></script>\n <script>\nwindow.dataLayer = window.dataLayer || [];\n\nfunction gtag() {dataLayer.push(arguments);}\ngtag('js', new Date());\nfunction handleChange(myRadio) {gtag('set',{'rating':myRadio.value});}\n</script>\n\n</body>\n</html>");


// End Of Main Template Body ----------------------------------------------------------------------
    }



    {
//Sub-Templates Initialization --------------------------------------------------------------------



//End of Sub-Templates Initialization -------------------------------------------------------------
    }

}

