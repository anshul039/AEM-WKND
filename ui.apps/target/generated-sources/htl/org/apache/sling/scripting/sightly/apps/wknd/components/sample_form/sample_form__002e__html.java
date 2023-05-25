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
package org.apache.sling.scripting.sightly.apps.wknd.components.sample_form;

import java.io.PrintWriter;
import java.util.Collection;
import javax.script.Bindings;

import org.apache.sling.scripting.sightly.render.RenderUnit;
import org.apache.sling.scripting.sightly.render.RenderContext;

public final class sample_form__002e__html extends RenderUnit {

    @Override
    protected final void render(PrintWriter out,
                                Bindings bindings,
                                Bindings arguments,
                                RenderContext renderContext) {
// Main Template Body -----------------------------------------------------------------------------

out.write("<html>\n<body>\n<div>\n\t<label>Field 1</label>\n\t<input class=\"form-text\" type=\"text\" id=\"field1\" name=\"field1\"/>\n</div>\n\n<div>\n\t<label>Field 2</label>\n\t<input class=\"form-text\" type=\"text\" id=\"field2\" name=\"field2\"/>\n</div>\n<div id=\"div1\"></div>\n<button onClick=\"handleClick()\">Button</button>\n\n<script>\n\nfunction handleClick() {\n\tvar field1 = document.getElementById('field1').value;\n\tvar field2 = document.getElementById('field2').value;\n\tfetch(\"/bin/wknd/sendEmail?field1=\"+field1+\"&field2=\"+field2, \n\t\t\t{method: 'GET'})\n\t.then(response => {console.log(response)})\n}\n</script>\n</body>\n</html>");


// End Of Main Template Body ----------------------------------------------------------------------
    }



    {
//Sub-Templates Initialization --------------------------------------------------------------------



//End of Sub-Templates Initialization -------------------------------------------------------------
    }

}

