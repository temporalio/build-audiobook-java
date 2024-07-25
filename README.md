# Build Your Own Audiobooks

This project creates a Temporal Worker that converts text into MP3 audiobooks using OpenAI's text-to-speech API (https://platform.openai.com/docs/api-reference/audio/createSpeech).
Temporal Workflows reliably transform your text file into high-quality audio.
Follow along with this project by reading the tutorial at [Temporal's learn site](https://learn.temporal.io/tutorials).

<!--
 LATER: [Temporal's learn site](https://learn.temporal.io/tutorials/java/audiobook/).
-->

Temporal provides an open-source solution that adds hassle-free fault mitigation to your projects.
You focus on your business logic instead of dealing with connection errors and other transitory issues.
Adding Temporal to OpenAI offers a smooth, efficient, and robust audiobook creation process.
OpenAI offers a fantastic text-to-speech experience. 
It includes a [variety of pre-built voices](https://platform.openai.com/docs/guides/text-to-speech/voice-options) and extensive languages support, so you can choose the perfect tone and rhythm for your audio files.
Together, these two technologies help you focus on creating great content, allowing the text-to-speech Temporal Worker to handle any issues that might arise during processing.

## Pre-requisites

Before starting this project:

- You need an active OpenAI API developer account and your bearer token.
- Be familiar with the Java programming language and the Gradle build tool.
- Make sure the Temporal CLI tool/development Server must be installed on your system.
  See [Getting Started with Java and Temporal](https://learn.temporal.io/getting_started/java/dev_environment/) to install up your tooling.

## Set up

Follow these steps to begin converting text to audio.

### Run the development server

Make sure the Temporal development server is running and is using a peristent store.
This makes sure that interrupted work can be picked up and continued without repeating steps, even if you experience server interruptions:

```
temporal server start-dev \
    --db-filename /path/to/your/temporal.db
```

Connect to the [Temporal Web UI](http://localhost:8233) at [http://localhost:8233](http://localhost:8233) to check that the server is working.  

### Instantiate your Bearer Token

Create an environment variable called OPEN_AI_BEARER_TOKEN to configure your OpenAI credentials.
If you set this value using a shell script, make sure to `source` the script so the variable carries over past the script execution.
The environment variable must set in the same shell where you'll run your worker. 

### Run the Worker

Build the worker with `gradle build` and then issue `gradle run` to start it running.
With the Worker running, you can submit jobs to the text-to-speech worker.

Please note, if the Worker can't fetch a bearer token it will fail at launch.
This prevents you running jobs and finding out you forgot to set the bearer token until you're well into the Workflow process.

## Submit narration jobs

Use the Temporal CLI tool to build audio from text files. 
Use `execute` to watch the execution in real time from the command line:

```
temporal workflow execute \
    --type TTSWorkflow \
    --task-queue tts-task-queue \
    --input '{"path": "/path/to/your/text-file.txt"}' \
    --workflow-id "tristam-shandy-tts"
```

* **type**: The name of this text-to-speech Workflow is **`TTSWorkflow`**.
* **task-queue**: This Worker polls the "**TTS_TASK_QUEUE**" Task Queue.
* **input**: Pass a JSON string with the following keys and values:
    * **path**: a /path/to/your/input/text-file.
* **workflow-id**: Set a descriptive name for your Workflow Id.
  This makes it easier to track your Workflow Execution in the Web UI.  
  The identifier you set doesn't affect the input text file or the output audio file names.

### Locate the generated file

The generated MP3 audio is placed in the same folder as your input text file.
It uses the same name replacing the `txt` extension with `mp3`.
If an output file already exists, it prevents name collisions with versioning.

The `TTSWorkflow` returns a string, the /path/to/your/output/audio-file.
Check the Web UI Input and Results section after the Workflow completes.
The results path is also listed as part of the CLI's `workflow execute` command and in the Worker output. 

### Cautions and notes

- For obvious reasons, don't mess with your input or output files while the workflow is running.
  Temporal is not a cure for bad judgement.
- The Workflow fails if you don't pass a valid text file named with a `txt` extension.
- The Workflow may fail if you don't use an absolute path to your text file
  Use `readlink` to convert a relative path to an absolute path:

  ```
  readlink -f <relative_path>
  ```
  
### Peeking at the process

There's a little Query message to check progress during long processes.
Submit in a separate terminal window or tab:

```
temporal workflow query \
    --type fetchMessage \
    --workflow-id YourWorkflowId
```

### Validate your audio output

The open source [checkmate](https://github.com/Sjord/checkmate) app lets you validate your generated MP3 file for errors.

```
$ mpck -v audio.mp3

SUMMARY: audio.mp3
    version                       MPEG v2.0
    layer                         3
    bitrate                       160000 bps
    samplerate                    24000 Hz
    frames                        23723
    time                          9:29.352
    unidentified                  0 b (0%)
    stereo                        yes
    size                          11120 KiB
    ID3V1                         no
    ID3V2                         no
    APEV1                         no
    APEV2                         no
    last frame                    
        offset                    11386560 b (0xadbec0)
        length                    480
    errors                        none
    result                        Ok
```

### Converting chapter files into a book

It makes sense to submit each chapter as a separate Workflow.
To combine your mp3 files, just concatenate them with [`ffmpeg`](https://ffmpeg.org/download.html).

1. Create a text file listing the files. For example:

```text
file 'chapter1.mp3'
file 'chapter2.mp3'
file 'chapter3.mp3'
...
``` 

2. Use `ffmpeg` to concatenate the audio:

```
ffmpeg -f concat -safe 0 -i chapters-list.txt -c copy fullbook.mp3
```

`ffmpeg` also allows you to convert your audio to other formats.
For example:

```
ffmpeg -i fullbook.mp3 fullbook.m4a
```

## Project Structure

```
src
└── main
    └── java
        └── ttsworker
            ├── model
            │   ├── ConversionStatus.java
            │   └── InputPayload.java
            ├── temporal
            │   ├── FileActivities.java
            │   ├── FileActivitiesImpl.java
            │   ├── TTSActivities.java
            │   ├── TTSActivitiesImpl.java
            │   ├── TTSWorkerApp.java
            │   ├── TTSWorkflow.java
            │   └── TTSWorkflowImpl.java
            └── utility
                ├── DataUtility.java
                ├── FileUtility.java
                └── TTSUtility.java
```
