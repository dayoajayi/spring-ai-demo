Extract following from given question and ground truth
            "tp": statements that are present in both the answer and the ground truth,
            "fp": statements present in the answer but not found in the ground truth,
            "fn": relevant statements found in the ground truth but omitted in the answer,

Output in only valid JSON format.

question: "What powers the sun and what is its primary function?"
answer: "The sun is powered by nuclear fission, similar to nuclear reactors on Earth, and its primary function is to provide light to the solar system."
ground_truth: "The sun is actually powered by nuclear fusion, not fission. In its core, hydrogen atoms fuse to form helium, releasing a tremendous amount of energy. This energy is what lights up the sun and provides heat and light, essential for life on Earth. The sun\'s light also plays a critical role in Earth\'s climate system and helps to drive the weather and ocean currents."
Extracted statements: \{"tp": \["The sun\'s primary function is to provide light"\], "fp": \["The sun is powered by nuclear fission", "similar to nuclear reactors on Earth"\], "fn": \["The sun is powered by nuclear fusion, not fission", "In its core, hydrogen atoms fuse to form helium, releasing a tremendous amount of energy", "This energy provides heat and light, essential for life on Earth", "The sun\'s light plays a critical role in Earth\'s climate system", "The sun helps to drive the weather and ocean currents"\]\}

question: "What is the boiling point of water?"
answer: "The boiling point of water is 100 degrees Celsius at sea level."
ground_truth: "The boiling point of water is 100 degrees Celsius (212 degrees Fahrenheit) at sea level, but it can change with altitude."
Extracted statements: \{"tp": \["The boiling point of water is 100 degrees Celsius at sea level"\], "fp": \[\], "fn": \["The boiling point can change with altitude", "The boiling point of water is 212 degrees Fahrenheit at sea level"\]\}

question: {question}
answer: {answer}
ground_truth: {ground_truth}
Extracted statements: