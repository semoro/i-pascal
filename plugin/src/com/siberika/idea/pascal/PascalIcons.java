/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.siberika.idea.pascal;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Apr 3, 2010
 * Time: 2:30:48 AM
 */
public interface PascalIcons {

    final String PATH = "/icons/";

    final Icon GENERAL = IconLoader.findIcon(PATH + "pascal_16x16.png");
    final Icon MODULE  = GENERAL;
    final Icon UNIT    = IconLoader.findIcon(PATH + "nunit.png");
    final Icon PROGRAM = IconLoader.findIcon(PATH + "nprogram.png");
    final Icon INCLUDE = IconLoader.findIcon(PATH + "ninclude.png");
    final Icon COMPILED = IconLoader.findIcon(PATH + "compiled.png");

    final Icon TYPE = IconLoader.findIcon(PATH + "ntype.png");
    final Icon VARIABLE = IconLoader.findIcon(PATH + "nvar.png");
    final Icon CONSTANT = IconLoader.findIcon(PATH + "nconst.png");
    final Icon PROPERTY = IconLoader.findIcon(PATH + "nproperty.png");
    final Icon ROUTINE = IconLoader.findIcon(PATH + "nroutine.png");
    final Icon INTERFACE = IconLoader.findIcon(PATH + "ninterface.png");
    final Icon CLASS = IconLoader.findIcon(PATH + "nclass.png");
    final Icon OBJECT = IconLoader.findIcon(PATH + "nobject.png");
    final Icon RECORD = IconLoader.findIcon(PATH + "nrecord.png");
    final Icon HELPER = IconLoader.findIcon(PATH + "nhelper.png");
}
