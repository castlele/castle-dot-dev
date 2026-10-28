---
title: Hello, world
date: 2026-10-01
tags: [meta, web, web/htlm]
description: Why this site exists and how it is built.
---

This is the first article. The site is a static export of
[Next.js](https://nextjs.org). Every article is a Markdown file in git.

![A pixel castle](/articles/hello-world/pixel-castle.svg)

## How an article becomes a page

The build reads the file, renders it to HTML and writes `index.html`:

```ts
const html = await renderMarkdown(article.body);
```

> Less code, fewer bugs.

## Notes

Asynchronous function/method is marked with `async` keyword. And the caller marks itself with `await` that actually means that this instructions will wait for the `async` method to finish.

By awaiting current thread can suspend it and do something else, saving a continuation for the future use. Continuations store local variables and other data to be able to continue work after suspension. Continuations are stored on the **heap**.

`await` has no guarantees to run continuation on the same thread:
1. `await` can't hold locks
2. thread specific data isn't preserved across an `await`

```swift
func asyncLetTest() async {
    async let value1 = getValueFromDataBase(1)
    async let value2 = getValueFromDataBase(2)

    let values = await (value1, value2)

    print("\(values.0), \(values.1)")
}

func getValueFromDataBase(_ number: Int) async -> Int {
    print("Waiting for task \(number)")
    try! await Task.sleep(nanoseconds: 1000)
    print("Task number \(number) completed")
    return random()
}


Task {
    //Prints:
    // Waiting for task 2
    // Waiting for task 1
    // Task number 1 completed
    // Task number 2 completed
    // 1804289383, 846930886
    await asyncLetTest()
}
```

> This is important to note that async function can start doing work on one thread and finish on another

Asynchronous function can run synchronous ones, but synchronous can't run asynchronous, because they don't have the power to create a suspension point. To allow that swift uses `Task` struct:

```swift
func viewDidLoad() {
	super.viewDidLoad()

	Task {
		await fetchImages()
	}
}
```

However, `Task` is not just a method of dealing with asynchronous functions from synchronous one. This is a fundamental concept for what is called **Structured concurrency**, where asynchronous code is structured as a hierarchy of tasks. This structuring allows parent tasks to manage child tasks and share information like status, context, priority and local values.

Every Task inherit its parent properties. So, if you want to create task with no parent inheritance, you should be using `Task.detached`

---
## See also
- [internal work of async/await](https://swiftrocks.com/how-async-await-works-internally-in-swift)
- [[swift_cooperative_thread_pool]]
- [[swift_actors]]
