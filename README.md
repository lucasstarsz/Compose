<div align="center">
    <img src="https://raw.githubusercontent.com/lucasstarsz/FXPad/main/.github/fxpad_png.png" 
width="25%" height="25%" alt="FXPad image"/>
    <h1>FXPad</h1>
    <span>A clean, simple text editor.<br/></span>
    <span>
        <a href="https://github.com/lucasstarsz/FXPad/releases">
            Releases</a>
        • 
        <a href="https://github.com/lucasstarsz/FXPad/blob/main/LICENSE">
            License</a>
        •
        <a href="https://github.com/lucasstarsz/FXPad/issues">
        	Request a Feature</a>
        •
        <a href="mailto:andrewrcdey@gmail.com">
            Contact Me
        </a>
    </span>
</div>
<h2 align="center">How To Run</h2>

#### Executable

- **Linux** & **Windows** installers are found [here](https://github.com/lucasstarsz/FXPad/releases).
- **Mac** users will need to build the application for themselves (I recommend using one of the batch scripts as a base), or otherwise run the application through the project (mentioned below).

#### Project

1. Download the repository, either by

    - [Pressing the green "Code" button](https://i.imgur.com/pw92PNf.png) to download the repository to your
      workspace (`.zip`, or downloading to [GitHub Desktop](https://desktop.github.com/)), or

    - Opening a terminal, and running

      ```bash
      git clone https://github.com/lucasstarsz/FXPad.git
      ```

      in the desired directory.

2. Open the project with [IntelliJ (2020.3 or later preferred)](https://www.jetbrains.com/idea/download/)

3. Select `Run` → `Run...` → `Edit Configurations...`

4. Select `Application` → `Main`

5. Set the VM Options to `--module-path "path/to/javafx-sdk-15/lib":out/production`
   
    - Don't see the option? You may need to click on `Modify options` in the top right of the window, and add a checkmark next to `Add VM options`.
    
6. Hit `Apply`, then `Run`. If everything goes as expected, you should see the project build and run!
