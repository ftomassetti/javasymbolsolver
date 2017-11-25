/**
 * Copyright 2017 The JavaParser Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.javaparser.symbolsolver.core.resolution;

import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserEnumDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javassistmodel.JavassistMethodDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionMethodDeclaration;

import java.util.List;

/**
 * @author Federico Tomassetti
 */
class ContextHelper {

    private ContextHelper() {
        // prevent instantiation
    }

    static MethodUsage resolveTypeVariables(Context context, ResolvedMethodDeclaration methodDeclaration, List<ResolvedType> parameterTypes) {
        if (methodDeclaration instanceof JavaParserMethodDeclaration) {
            return ((JavaParserMethodDeclaration) methodDeclaration).resolveTypeVariables(context, parameterTypes);
        } else if (methodDeclaration instanceof JavassistMethodDeclaration) {
            return ((JavassistMethodDeclaration) methodDeclaration).resolveTypeVariables(context, parameterTypes);
        } else if (methodDeclaration instanceof JavaParserEnumDeclaration.ValuesMethod) {
            return ((JavaParserEnumDeclaration.ValuesMethod) methodDeclaration).resolveTypeVariables(context, parameterTypes);
        } else if (methodDeclaration instanceof ReflectionMethodDeclaration) {
            return ((ReflectionMethodDeclaration) methodDeclaration).resolveTypeVariables(context, parameterTypes);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
