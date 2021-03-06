package org.terasology.audio;

import static org.lwjgl.openal.AL10.AL_BITS;
import static org.lwjgl.openal.AL10.AL_CHANNELS;
import static org.lwjgl.openal.AL10.AL_FREQUENCY;
import static org.lwjgl.openal.AL10.AL_SIZE;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGetBufferi;

import org.terasology.asset.AssetUri;

public abstract class AbstractSound implements Sound {

    // TODO: Do we have proper support for unloading sounds (as mods are changed?)
    private static int bufferAmount = 0;

    private AssetUri uri;
    private int bufferId = 0;
    protected int length = 0;

    public AbstractSound(AssetUri uri, int bufferId) {
        this.uri = uri;
        this.bufferId = bufferId;

        OpenALException.checkState("Allocating sound buffer");

        bufferAmount++;
    }

    @Override
    public int getLength() {
        if (length == 0 && bufferId != 0) { // only if buffer is already initialized
            int bits = getBufferBits();
            int size = getBufferSize();
            int channels = getChannels();
            int frequency = getSamplingRate();

            length = size / channels / (bits / 8) / frequency;
        }

        return length;
    }

    @Override
    public int getChannels() {
        return alGetBufferi(bufferId, AL_CHANNELS);
    }

    @Override
    public int getSamplingRate() {
        return alGetBufferi(bufferId, AL_FREQUENCY);
    }

    @Override
    public int getBufferId() {
        return bufferId;
    }

    public int getBufferBits() {
        return alGetBufferi(bufferId, AL_BITS);
    }

    @Override
    public int getBufferSize() {
        return alGetBufferi(bufferId, AL_SIZE);
    }

    @Override
    public AssetUri getURI() {
        return uri;
    }

    @Override
    public void reset() {
    }

    @Override
    protected void finalize() throws Throwable {
        if (bufferId != 0) {
            alDeleteBuffers(bufferId);
        }
    }
}
