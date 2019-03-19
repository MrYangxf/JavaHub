package top.yangxf.interest.flow;

import top.yangxf.interest.flow.task.TaskFlowManager;
import top.yangxf.interest.flow.task.annotation.Task;
import top.yangxf.interest.flow.task.annotation.TaskEntry;
import top.yangxf.interest.flow.task.annotation.TaskParam;
import top.yangxf.interest.flow.task.annotation.VariableScope;

import static top.yangxf.interest.flow.task.TaskFlowManager.named;

/**
 * @author yangxf
 */
public class Test {
    public static void main(String[] args) {
        TaskFlowManager manager = TaskFlowManager.getInstance();
        FlowContext ctx = FlowContext.newContext();
        ctx.putAttr("a", 8)
           .putAttr("b", 9);
        Flow flow = manager.build(ctx, named("ComputeTask"), named("print"), named("ComputeTask"));
        flow.start();
    }

    @Task
    public static class ComputeTask {

        @TaskEntry(order = 1, returnName = "computeResult", scope = VariableScope.GLOBAL, override = false)
        public String exec(FlowContext ctx, @TaskParam("a") int a, @TaskParam("b") int b) {
            System.out.println((String) ctx.getAttr("computeResult"));
            return a + b + "";
        }
    }

    @Task("print")
    public static class PrintTask {

        @TaskEntry(order = 0)
        public void print(@TaskParam("computeResult") String text) throws InterruptedException {
            System.out.println(text);
            Thread.sleep(500);
        }
    }
}
