# JobQueue #

## Introduction ##

The JobQueue project is a library which allows Java command execution to be distributed from producers to consumers on multiple machines via a persistent queue.

To use the library an implementation of a persistent queue is required to be implemented - a subclass of [PersistentQueuedJobCommandQueue](/JobQueue/src/main/java/com/echobox/jobqueue/PersistentQueuedJobCommandQueue.java)
.     

You can then wrap the data and business logic needed to perform a task inside [JobCommand](/JobQueue/src/main/java/com/echobox/jobqueue/commands/JobCommand.java) instances - these instances can then be sent to the persistent queue for remote execution, or executed locally if required.

This framework allows JobCommands to be sent by producers to the persistent queue, and allows the execution of these tasks on consumers to be managed,  with the runtime status of the task being passed back to the producer via the persistent queue.

We provide a default implementation of PersistentQueuedJobCommandQueue (  [PersistentQueuedJobCommandQueueImpl](/JobQueue/src/main/java/com/echobox/jobqueue/PersistentQueuedJobCommandQueueImpl.java) ) as part of this library - this delegates to a [PersistentJobCommandQueue](/JobQueue/src/main/java/com/echobox/jobqueue/PersistentJobCommandQueue.java) which is an easier interface to work with.  If this default implementation is used it will be an instance of PersistentJobCommandQueue which will need to be implemented in order to use the library.

## Configuring the framework for your application ##

To get started with the library, a number of domain classes must be defined for your application, such as an enum for the different types of Job, a class or enum to represent the different queue types you will need, and the type of id that will be used to uniquely identify jobs on the queue.   ( see the com.echobox.jobqueue.demo.domain package in the JobQueueDemo project)

Once these classes have been defined,  simple extensions of our base interfaces/classes provided in the library can be created which bind these domain objects to the framework classes ( see the com.echobox.jobqueue.demo.domain.binding  package in the JobQueueDemo project) 

Finally an implementation of PersistentJobCommandQueue must be provided as mentioned above ( assuming it is the default PersistentQueuedJobCommandQueueImpl which is used) -  see the  com.echobox.jobqueue.demo.impl package in the JobQueueDemo project.

Once these classes are in place, you can start wrapping your data and business logic inside JobCommand instances in order to create components that can be sent to the queue and executed remotely.

## Overview of JobCommands ##

A JobCommand is an executable command that encapsulates the business logic and data needed to accomplish a task.

JobCommands can be executed so they run locally, either synchronously or asynchronously.

JobCommands can be also executed remotely by configuring a producer to send them to a persistent queue - they can then be picked up by consumers and executed on the consumer machine.

In order to send a job command to a queue, the producer can wrap the job command in a decorator - a “[QueuedJobCommand](/JobQueue/src/main/java/com/echobox/jobqueue/commands/QueuedJobCommand.java) " - which is instantiated with a reference to the command to be wrapped, along with a reference to a PersistentQueuedJobCommandQueue,  and details of the queue we wish to send the command to.

This QueuedJobCommand can be executed just like any other JobCommand - but on execution, instead of executing the wrapped JobCommand locally, the command is sent to the persitent queue, and the progress monitored. 

If a QueuedJobCommand is executed synchronously, the execute method waits for the persistent queue to report successful completion. Any exceptions which occur on the consumer are written back to the persistent queue and are wrapped and rethrown at the producer.

If a QueuedJobCommand is executed asynchronously, the execute method returns immediately, with a handle to the future success indicator.   When a producer inspecting the future success, any executions which occur on the consumer are written back to the persistent queue and are wrapped and rethrown at the producer.


## Useful JobCommand base and wrapper classes ##

JobCommand is an abstract base class, so to create a new type of JobCommand simply create a new subclass, and provide the implementations of the required methods.

We provide some useful base classes as part of this library, such as [WorkerJobCommand](/JobQueue/src/main/java/com/echobox/jobqueue/commands/WorkerJobCommand.java) ,  and [SubtaskCoordinatorJobCommand](/JobQueue/src/main/java/com/echobox/jobqueue/commands/SubtaskCoordinatorJobCommand.java) 

WorkerJobCommands are convenient base classes for commands we wish to send to a queue that are executable independently - ie. they don't have dependencies on other commands.

The business logic for a WorkerJobCommand is implemented inside the doWork method.  Any exceptions which occur while processing this work do not need to be handled here unless there is a specific
requirement to do so - they will be handled by the framework and will be wrapped and rethrown at the producer.

A SubtaskCoordinatorJobCommand is a command which has the role of coordinating other commands ("subtasks").

The subtasks to be coordinated can be any type of JobCommand - they can even be "QueuedJobCommand" themselves - 
this is useful when we want to send multiple commands on the queue to be picked up by multiple consumers, and coordinate them as a batch on the producer side.

Similarly, as a SubtaskCoordinatorJobCommand is itself a JobCommand, it can be wrapped by a QueuedJobCommand and sent to the queue for processing by consumers.  This is useful when we want to send a single command to the queue which gets "unpacked" by a consumer into smaller subtasks which can be coordinated as a batch by that consumer.

We also provide AsynchronousJobCommandAdapter which is a decorator for any type of JobCommand - on execution ( of the execute method), this command performs asynchronous execution of the decorated command - ie. this decorator adapts any JobCommand so that it executes asynchronously by default.

Finally there is the CommandWithRetries decorator which performs a specified number of retry attempts on the decorated command on execution.

### SubtaskCoordinatorJobCommand and retry behaviour ###

For JobCommand decorators that wrap a single command to be managed, the 
execution behaviour on a retry attempt is easy to define - the single decorated command is executed again.  The single decorated command has a single completion status, so it is straightforward to determine the behaviour on retry.

For SubtaskCoordinatorJobCommand commands however, it is not as clear how to define their runtime status without explicit specification, and it isn't as clear how to define the behaviour on retry.

For example, if any of the subtasks fail, is that something that we want to cause a failed status at the coordinator level?  Do we want to ignore the subtask error and continue with other subtasks, log a warning and continue, or halt execution of the coordinator altogether when a subtask fails?

When creating a custom SubtaskCoordinatorJobCommand we can define the desired behaviour by implementing the createSubtaskCoordinatorBehaviour() method, for example:

```

 /* (non-Javadoc)
   * @see sh.ebx.jobqueue.commands.SubtaskCoordinatorJobCommand#createSubtaskCoordinatorBehaviour()
   */
  @Override
  public SubtaskCoordinatorBehaviour createSubtaskCoordinatorBehaviour() {
    return new SubtaskCoordinatorBehaviour(RaiseWarning.ON_SUBTASK_ERROR,
        InterruptWaitForCompletion.ON_FIRST_WARNING, IfWaitForCompletionIsInterrupted.DONT_COMPLETE,
        IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS);
  }


```
There are 4 aspects to this behaviour to consider, as defined by the 4 enums passed into the SubtaskCoordinatorBehaviour constructor.   

The various combinations of these enums allow flexible custom behaviours to be defined - please not that not all possible combinations make sense or are valid.

The 4 enums are described below:

#### RaiseWarning ####  

This enum defines the desired warning behaviour on a subtask error - do we want to ignore the subtask error ( RaiseWarning.NEVER ) or raise a warning ( RaiseWarning.ON_SUBTASK_ERROR ).

#### InterruptWaitForCompletion ####

If we have defined RaiseWarning.NEVER, the value of the InterruptWaitForCompletion enum has no effect on the behaviour - however if we have defined RaiseWarning.ON_SUBTASK_ERROR, this enum allows the desired behaviour on the warning to be defined.  Do we want to continue processing other subtasks ( InterruptWaitForCompletion.NEVER ) or interrupt the process that is waiting for completion ( InterruptWaitForCompletion.ON_FIRST_WARNING )

#### IfWaitForCompletionIsInterrupted ####

If we have specified that we never want to interrupt the wait for completion on subtask error, the value of the IfWaitForCompletionIsInterrupted has no effect.  However if we are have specified InterruptWaitForCompletion.ON_FIRST_WARNING, the IfWaitForCompletionIsInterrupted enum defines whether we want to stop waiting for completion but leave the coordinator task as uncompleted ( IfWaitForCompletionIsInterrupted.DONT_COMPLETE), or whether we want to mark the coordinator task as completed with error ( IfWaitForCompletionIsInterrupted.COMPLETE_WITH_ERROR ).    Leaving the parent coordinator task as uncompleted allows the potential for a retry attempt on the coordinator continuing where it left off for example, whereas marking it as completed with error will cause a retry attempt to retry all subtasks

#### IfRetriedBeforeCompletion ####

The value of this enum defines what we want to do if a subtask coordinator command is retried before it has been marked as completed ( for example if a subtask has errored and IfWaitForCompletionIsInterrupted.DONT_COMPLETE has been specified, or in the event a producer wishes to trigger a retry in order to re-execute any substasks which have completed ).  

In the event a retry attempt is made on a coordinator before all the subtasks have completed, do we want to simply continue to wait ( IfRetriedBeforeCompletion.JUST_CONTINUE_TO_WAIT ), retry all subtasks ( IfRetriedBeforeCompletion.RETRY_ALL_SUBTASKS ),  retry all subtasks that have completed, either successfully or unsuccesfully ( IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_SUBTASKS ) or retry only those subtasks which have failed ( IfRetriedBeforeCompletion.RETRY_ALL_COMPLETED_WITH_ERROR_SUBTASKS ).  
  

## See the JobQueueDemo project for an example of how to use this library to queue commands on a producer and execute remotely on a consumer ##
