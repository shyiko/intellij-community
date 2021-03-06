/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package com.intellij.properties;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.lang.properties.PropertiesQuickFixFactory;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.lang.properties.psi.Property;
import com.intellij.psi.PsiElement;

import java.util.List;

public class CorePropertiesQuickFixFactory extends PropertiesQuickFixFactory {
  @Override
  public LocalQuickFix createCreatePropertyFix(PsiElement element, String key, List<PropertiesFile> files) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IntentionAction createRemovePropertyFix(Property property) {
    throw new UnsupportedOperationException();
  }

  @Override
  public LocalQuickFix createRemovePropertyLocalFix() {
    throw new UnsupportedOperationException();
  }
}
