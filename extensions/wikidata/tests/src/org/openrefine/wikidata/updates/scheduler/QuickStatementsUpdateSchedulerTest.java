/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Antonin Delpeuch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.openrefine.wikidata.updates.scheduler;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.openrefine.wikidata.updates.TermedStatementEntityUpdate;
import org.openrefine.wikidata.updates.ItemUpdateBuilder;
import org.testng.annotations.Test;

public class QuickStatementsUpdateSchedulerTest extends UpdateSchedulerTest {

    @Test
    public void testNoNewItem()
            throws ImpossibleSchedulingException {
    	TermedStatementEntityUpdate updateA = new ItemUpdateBuilder(existingIdA).addStatement(sAtoB).build();
        TermedStatementEntityUpdate updateB = new ItemUpdateBuilder(existingIdB).addStatement(sBtoA).build();
        List<TermedStatementEntityUpdate> scheduled = schedule(updateA, updateB);
        assertEquals(Arrays.asList(updateA, updateB), scheduled);
    }

    @Test
    public void testSplitUpdate()
            throws ImpossibleSchedulingException {
    	TermedStatementEntityUpdate updateA = new ItemUpdateBuilder(existingIdA).addStatement(sAtoNewA).addStatement(sAtoNewB).build();
    	TermedStatementEntityUpdate newUpdateA = new ItemUpdateBuilder(newIdA).build();
        TermedStatementEntityUpdate newUpdateB = new ItemUpdateBuilder(newIdB).build();
        TermedStatementEntityUpdate splitUpdateA = new ItemUpdateBuilder(existingIdA).addStatement(sAtoNewA).build();
        TermedStatementEntityUpdate splitUpdateB = new ItemUpdateBuilder(existingIdA).addStatement(sAtoNewB).build();
        List<TermedStatementEntityUpdate> scheduled = schedule(updateA);
        assertSetEquals(Arrays.asList(newUpdateA, splitUpdateA, newUpdateB, splitUpdateB), scheduled);
    }

    @Test(expectedExceptions = ImpossibleSchedulingException.class)
    public void testImpossibleForQS()
            throws ImpossibleSchedulingException {
    	TermedStatementEntityUpdate update = new ItemUpdateBuilder(newIdA).addStatement(sNewAtoNewB).build();
        schedule(update);
    }

    @Test
    public void testSelfEditOnNewITem()
            throws ImpossibleSchedulingException {
    	TermedStatementEntityUpdate update = new ItemUpdateBuilder(newIdA).addStatement(sNewAtoNewA).build();
        assertEquals(Arrays.asList(update), schedule(update));
    }

    @Override
    public UpdateScheduler getScheduler() {
        return new QuickStatementsUpdateScheduler();
    }
}
